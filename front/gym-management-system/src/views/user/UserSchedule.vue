<template>
  <div class="venue-schedule-container">
    <el-card>
      <div slot="header" class="card-header">
        <h2>场地安排</h2>
      </div>
      <!-- 搜索筛选区域 -->
      <el-form :inline="true" class="search-form" size="small">
        <el-form-item label="场地类型">
          <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchSchedule">
            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="场地名称">
          <el-select v-model="searchForm.venueId" placeholder="选择场地" clearable @change="searchSchedule">
            <el-option v-for="venue in filteredVenues" :key="venue.id" :label="venue.name" :value="venue.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchSchedule">查询安排</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 日期导航 -->
      <div class="date-navigation">
        <el-button icon="el-icon-arrow-left" @click="changeWeek(-1)"></el-button>
        <span class="date-range">{{ formatDateRange(currentWeekStart, 6) }}</span>
        <el-button icon="el-icon-arrow-right" @click="changeWeek(1)"></el-button>
        <el-button type="text" @click="resetToCurrentWeek">回到本周</el-button>
      </div>
      <!-- 日历表格 -->
      <div class="schedule-calendar" v-loading="loading">
        <table class="schedule-table">
          <thead>
          <tr>
            <th class="time-column">时间段</th>
            <th v-for="(day, index) in weekDays" :key="index">
              {{ day.dayOfWeek }}<br>
              <small>{{ day.date }}</small>
            </th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="timeSlot in timeSlots" :key="timeSlot.id">
            <td class="time-column">{{ timeSlot.timeRange }}</td>
            <td v-for="(day, dayIndex) in weekDays" :key="dayIndex" :class="getCellClass(getScheduleForTimeAndDay(timeSlot, day))">
              <div v-if="getScheduleForTimeAndDay(timeSlot, day)" class="schedule-cell-content" @click="showScheduleDetail(getScheduleForTimeAndDay(timeSlot, day))">
                {{ getScheduleForTimeAndDay(timeSlot, day).venueName }}
                <div class="schedule-status">{{ getStatusText(getScheduleForTimeAndDay(timeSlot, day).status) }}</div>
              </div>
              <div v-else class="schedule-cell-empty"></div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <!-- 图例说明 -->
      <div class="schedule-legend">
        <div class="legend-item"><div class="legend-color available"></div><div class="legend-text">可预约</div></div>
        <div class="legend-item"><div class="legend-color booked"></div><div class="legend-text">已预约</div></div>
        <div class="legend-item"><div class="legend-color in-use"></div><div class="legend-text">使用中</div></div>
        <div class="legend-item"><div class="legend-color special"></div><div class="legend-text">特殊场地</div></div>
        <div class="legend-item"><div class="legend-color maintenance"></div><div class="legend-text">维护中</div></div>
      </div>
    </el-card>
    <!-- 场地详情弹窗 -->
    <el-dialog title="场地安排详情" v-model:visible="scheduleDetailVisible" width="500px">
      <div v-if="selectedSchedule" class="schedule-detail">
        <h3>{{ selectedSchedule.venueName }}</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="场地类型">{{ getVenueTypeLabel(selectedSchedule.type) }}</el-descriptions-item>
          <el-descriptions-item label="日期">{{ selectedSchedule.date }}</el-descriptions-item>
          <el-descriptions-item label="时间段">{{ selectedSchedule.timeSlot }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedSchedule.status)">
              {{ getStatusText(selectedSchedule.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedSchedule.bookedBy" label="预约人">
            {{ selectedSchedule.bookedBy }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedSchedule.numberOfPeople" label="使用人数">
            {{ selectedSchedule.numberOfPeople }} 人
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedSchedule.remarks" label="备注">
            {{ selectedSchedule.remarks }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="schedule-actions" v-if="selectedSchedule.status === 'AVAILABLE'">
          <el-button type="primary" @click="goToBooking">去预约</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="scheduleDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'UserSchedule',
  data() {
    return {
      searchForm: { venueType: '', venueId: '' },
      venueTypes: [
        { value: 'basketball', label: '篮球场' },
        { value: 'football', label: '足球场' },
        { value: 'badminton', label: '羽毛球场' },
        { value: 'tennis', label: '网球场' },
        { value: 'swimming', label: '游泳池' },
        { value: 'table_tennis', label: '乒乓球室' }
      ],
      venues: [],
      currentWeekStart: new Date(),
      timeSlots: [],
      scheduleData: [],
      loading: false,
      selectedSchedule: null,
      scheduleDetailVisible: false,
      scheduleCache: new Map(),
      debounceTimer: null,
      lastSearchParams: null
    }
  },
  computed: {
    filteredVenues() {
      if (!this.searchForm.venueType) return this.venues;
      return this.venues.filter(venue => venue.type === this.searchForm.venueType);
    },
    weekDays() {
      const days = [];
      const weekStart = new Date(this.currentWeekStart);
      const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      for (let i = 0; i < 7; i++) {
        const date = new Date(weekStart);
        date.setDate(weekStart.getDate() + i);
        days.push({
          date: this.formatDate(date),
          dayOfWeek: dayNames[date.getDay()],
          fullDate: date
        });
      }
      return days;
    }
  },
  created() {
    this.setWeekStartToSunday();
    this.loadVenueData();
    this.initTimeSlots();
    this.loadScheduleData();
  },
  methods: {
    setWeekStartToSunday() {
      const today = new Date();
      const day = today.getDay();
      if (day !== 0) today.setDate(today.getDate() - day);
      today.setHours(0, 0, 0, 0);
      this.currentWeekStart = today;
    },
    loadVenueData() {
      this.loading = true;
      axios.get('/api/venues')
          .then(response => {
            if (response.data.code === 200 && response.data.data) {
              const pageData = response.data.data;
              this.venues = pageData.records || [];
              this.venues.forEach(venue => {
                if (venue.type) {
                  const typeOption = this.venueTypes.find(t => t.value === venue.type);
                  if (typeOption) venue.type = typeOption.label;
                }
              });
            } else {
              this.venues = [];
            }
          })
          .catch(error => {
            this.$message.error('加载场馆数据失败: ' + error.message);
          })
          .finally(() => {
            this.loading = false;
          });
    },
    initTimeSlots() {
      const timeSlots = [];
      let startTime = new Date();
      startTime.setHours(8, 0, 0, 0);
      const endTime = new Date();
      endTime.setHours(22, 0, 0, 0);
      while (startTime < endTime) {
        const endTimeSlot = new Date(startTime.getTime() + 60 * 60000);
        timeSlots.push({
          id: timeSlots.length + 1,
          timeRange: `${this.formatTime(startTime)} - ${this.formatTime(endTimeSlot)}`
        });
        startTime = endTimeSlot;
      }
      this.timeSlots = timeSlots;
    },
    formatTime(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    loadScheduleData() {
      this.loading = true;
      const params = {
        startDate: this.formatDate(this.currentWeekStart),
        endDate: this.formatDate(new Date(this.currentWeekStart.getTime() + 6 * 24 * 60 * 60 * 1000))
      };
      if (this.searchForm.venueType) params.venueType = this.searchForm.venueType;
      if (this.searchForm.venueId) params.venueId = this.searchForm.venueId;

      const cacheKey = JSON.stringify(params);
      if (this.scheduleCache.has(cacheKey)) {
        this.scheduleData = this.scheduleCache.get(cacheKey);
        this.loading = false;
        return;
      }

      axios.get('/api/venue-schedules/weekly', { params })
          .then(response => {
            if (response.data && response.data.code === 200) {
              this.scheduleData = response.data.data || [];
              this.scheduleCache.set(cacheKey, this.scheduleData);
            } else {
              this.$message.error(response.data.msg || '获取场地安排失败');
            }
          })
          .catch(error => {
            this.$message.error('获取场地安排失败：' + (error.response?.data?.msg || error.message));
          })
          .finally(() => {
            this.loading = false;
          });
    },
    searchSchedule() {
      if (this.debounceTimer) {
        clearTimeout(this.debounceTimer);
      }
      this.debounceTimer = setTimeout(() => {
        this.loadScheduleData();
      }, 300);
    },
    resetSearch() {
      this.searchForm = { venueType: '', venueId: '' };
      this.scheduleCache.clear();
      this.searchSchedule();
    },
    changeWeek(offset) {
      const newDate = new Date(this.currentWeekStart);
      newDate.setDate(newDate.getDate() + (offset * 7));
      this.currentWeekStart = newDate;
      this.scheduleCache.clear();
      this.loadScheduleData();
    },
    resetToCurrentWeek() {
      this.setWeekStartToSunday();
      this.loadScheduleData();
    },
    getScheduleForTimeAndDay(timeSlot, day) {
      return this.scheduleData.find(schedule =>
          schedule.date === day.date &&
          schedule.timeSlot === timeSlot.timeRange
      );
    },
    getCellClass(schedule) {
      if (!schedule) return 'schedule-cell-empty';
      const statusClassMap = {
        'AVAILABLE': 'schedule-cell-available',
        'BOOKED': 'schedule-cell-booked',
        'IN_USE': 'schedule-cell-in-use',
        'SPECIAL': 'schedule-cell-special',
        'MAINTENANCE': 'schedule-cell-maintenance'
      };
      return `schedule-cell ${statusClassMap[schedule.status] || ''}`;
    },
    showScheduleDetail(schedule) {
      this.selectedSchedule = schedule;
      this.scheduleDetailVisible = true;
    },
    goToBooking() {
      if (this.selectedSchedule) {
        this.$router.push({
          path: '/user/venues',
          query: {
            venueId: this.selectedSchedule.venueId,
            date: this.selectedSchedule.date
          }
        });
        this.scheduleDetailVisible = false;
      }
    },
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    formatDateRange(startDate, days) {
      const start = new Date(startDate);
      const end = new Date(startDate);
      end.setDate(start.getDate() + days);
      return `${this.formatDate(start)} 至 ${this.formatDate(end)}`;
    },
    getVenueTypeLabel(type) {
      const typeOption = this.venueTypes.find(t => t.value === type);
      return typeOption ? typeOption.label : type;
    },
    getStatusText(status) {
      const statusMap = {
        'AVAILABLE': '可预约',
        'BOOKED': '已预约',
        'IN_USE': '使用中',
        'SPECIAL': '特殊场地',
        'MAINTENANCE': '维护中',
        'PAST': '已过期',
        'PENDING': '待确认'
      };
      return statusMap[status] || status;
    },
    getStatusType(status) {
      const typeMap = {
        'AVAILABLE': 'success',
        'BOOKED': 'warning',
        'IN_USE': 'danger',
        'SPECIAL': 'info',
        'MAINTENANCE': 'info',
        'PAST': '',
        'PENDING': 'warning'
      };
      return typeMap[status] || '';
    }
  }
}
</script>

<style scoped>
.schedule-table {
  width: 100%;
  border-collapse: collapse;
}
.schedule-table th, .schedule-table td {
  border: 1px solid #ebeef5;
  text-align: center;
  padding: 8px 0;
}
.time-column {
  width: 90px;
  background: #fafafa;
  font-weight: bold;
}
.schedule-cell { text-align: center; min-width: 80px; min-height: 48px; padding: 4px 0; }
.schedule-cell-content { cursor: pointer; border-radius: 4px; padding: 2px 0; }
.schedule-cell-available { background: #e6f7e6; color: #389e0d; }
.schedule-cell-booked { background: #fffbe6; color: #d48806; }
.schedule-cell-in-use { background: #ffeaea; color: #cf1322; }
.schedule-cell-special { background: #e6f4ff; color: #096dd9; }
.schedule-cell-maintenance { background: #f5f5f5; color: #888; }
.schedule-cell-empty { background: #fff; }
.schedule-legend {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  margin-right: 4px;
}
.legend-color.available { background: #e6f7e6; border: 1px solid #389e0d; }
.legend-color.booked { background: #fffbe6; border: 1px solid #d48806; }
.legend-color.in-use { background: #ffeaea; border: 1px solid #cf1322; }
.legend-color.special { background: #e6f4ff; border: 1px solid #096dd9; }
.legend-color.maintenance { background: #f5f5f5; border: 1px solid #888; }
</style>