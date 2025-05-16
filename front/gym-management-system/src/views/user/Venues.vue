<template>
  <div class="venue-booking-container">
    <el-card>
      <div slot="header" class="card-header">
        <h2>场地预约</h2>
      </div>
      
      <!-- 搜索筛选区域 -->
      <el-form :inline="true" class="search-form" size="small">
        <el-form-item label="场地类型">
          <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchVenues">
            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            :picker-options="datePickerOptions"
            @change="searchVenues">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchVenues">查询场地</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 场地列表 -->
      <el-table 
        v-loading="loading" 
        :data="venueList" 
        style="width: 100%"
        :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
        border>
        <el-table-column prop="name" label="场地名称" width="150"></el-table-column>
        <el-table-column prop="type" label="场地类型" width="120"></el-table-column>
        <el-table-column prop="description" label="场地描述"></el-table-column>
        <el-table-column prop="pricePerHour" label="价格" width="120">
          <template #default="scope">
            {{ scope.row.pricePerHour }} 元/小时
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.isAvailable ? 'success' : 'danger'">
              {{ scope.row.isAvailable ? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="showTimeSlots(scope.row)" :disabled="!scope.row.isAvailable">预约</el-button>
            <el-button type="info" size="mini" @click="showVenueDetails(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 时间段选择弹窗 -->
    <el-dialog title="选择预约时间段" :visible.sync="timeSlotDialogVisible" width="650px">
      <div v-if="selectedVenue">
        <h4>场地: {{ selectedVenue.name }}</h4>
        <p>日期: {{ formatDate(searchForm.date) }}</p>
        
        <el-divider content-position="left">可用时间段</el-divider>
        
        <div class="time-slots-container">
          <el-row :gutter="10">
            <el-col :span="8" v-for="timeSlot in timeSlots" :key="timeSlot.startTime">
              <div 
                :class="['time-slot', {
                  'available': timeSlot.status === 'AVAILABLE',
                  'booked': timeSlot.status === 'BOOKED',
                  'special': timeSlot.status === 'SPECIAL',
                  'selected': selectedTimeSlot && selectedTimeSlot.startTime === timeSlot.startTime
                }]"
                @click="timeSlot.status === 'AVAILABLE' && selectTimeSlot(timeSlot)">
                {{ timeSlot.startTime }} - {{ timeSlot.endTime }}
                <div class="time-slot-price" v-if="timeSlot.status === 'AVAILABLE'">
                  {{ timeSlot.price }}元/小时
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <el-divider></el-divider>
        
        <el-form :model="bookingForm" :rules="bookingRules" ref="bookingForm" label-width="100px" size="small">
          <el-form-item label="备注信息" prop="remarks">
            <el-input type="textarea" v-model="bookingForm.remarks" placeholder="请输入预约备注信息"></el-input>
          </el-form-item>
        </el-form>
        
        <div class="booking-summary" v-if="selectedTimeSlot">
          <p>预约时间: {{ selectedTimeSlot.startTime }} - {{ selectedTimeSlot.endTime }}</p>
          <p>预计费用: {{ calculateEstimatedCost() }} 元</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="timeSlotDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBooking" :disabled="!selectedTimeSlot">确认预约</el-button>
      </span>
    </el-dialog>
    
    <!-- 场地详情弹窗 -->
    <el-dialog title="场地详情" :visible.sync="venueDetailDialogVisible" width="500px">
      <div v-if="selectedVenue" class="venue-detail">
        <div class="venue-image">
          <img :src="selectedVenue.imageUrl || '/img/default-venue.jpg'" alt="场地图片">
        </div>
        <div class="venue-info">
          <h3>{{ selectedVenue.name }}</h3>
          <p><strong>类型:</strong> {{ selectedVenue.type }}</p>
          <p><strong>描述:</strong> {{ selectedVenue.description || '暂无描述' }}</p>
          <p><strong>价格:</strong> {{ selectedVenue.pricePerHour }} 元/小时</p>
          <p><strong>状态:</strong> 
            <el-tag :type="selectedVenue.isAvailable ? 'success' : 'danger'">
              {{ selectedVenue.isAvailable ? '可用' : '不可用' }}
            </el-tag>
          </p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UserVenues',
  data() {
    return {
      venues: [],
      loading: false,
      searchForm: {
        venueType: '',
        date: this.getTodayDate()
      },
      venueTypes: [
        { value: 'basketball', label: '篮球场' },
        { value: 'football', label: '足球场' },
        { value: 'badminton', label: '羽毛球场' },
        { value: 'tennis', label: '网球场' },
        { value: 'swimming', label: '游泳池' },
        { value: 'table_tennis', label: '乒乓球室' }
      ],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      venueList: [],
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        }
      },
      timeSlotDialogVisible: false,
      selectedVenue: null,
      timeSlots: [],
      selectedTimeSlot: null,
      bookingForm: {
        remarks: ''
      },
      bookingRules: {
        remarks: [
          { required: false, message: '请输入预约备注信息', trigger: 'blur' }
        ]
      },
      venueDetailDialogVisible: false,
      apiBaseUrl: '/api/venues'
    }
  },
  created() {
    this.loadVenues()
  },
  methods: {
    getTodayDate() {
      const today = new Date()
      const year = today.getFullYear()
      const month = (today.getMonth() + 1).toString().padStart(2, '0')
      const day = today.getDate().toString().padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr
      if (!(date instanceof Date) || isNaN(date)) {
        return ''
      }
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatDateWithChinese(dateStr) {
      if (!dateStr) return ''
      const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr
      if (!(date instanceof Date) || isNaN(date)) {
        return ''
      }
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
    },
    async loadVenues() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.currentPage,
          size: this.pagination.pageSize
        }
        if (this.searchForm.venueType) {
          params.type = this.searchForm.venueType
        }
        const response = await axios.get(this.apiBaseUrl, {
          params,
          headers: {
            'Content-Type': 'application/json'
          }
        })
        if (response.data && response.data.code === 200) {
          const pageData = response.data.data
          this.venueList = Array.isArray(pageData.records) ? pageData.records.slice() : []
          this.pagination.total = pageData.total || 0
          console.log('venueList after assign:', this.venueList)
        } else {
          this.$message.error(response.data.msg || '加载场地信息失败')
          console.error('返回数据格式不正确:', response.data)
        }
      } catch (error) {
        this.$message.error(error.response?.data?.msg || '加载场地信息失败')
        console.error('加载场地信息失败:', error)
      } finally {
        this.loading = false
      }
    },
    searchVenues() {
      this.pagination.currentPage = 1
      this.loadVenues()
    },
    resetSearch() {
      this.searchForm = {
        venueType: '',
        date: this.getTodayDate()
      }
      this.searchVenues()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadVenues()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadVenues()
    },
    async showTimeSlots(venue) {
      this.selectedVenue = venue
      this.timeSlotDialogVisible = true
      this.selectedTimeSlot = null
      this.bookingForm.remarks = ''
      await this.loadTimeSlots()
    },
    async loadTimeSlots() {
      try {
        const response = await axios.get(`${this.apiBaseUrl}/${this.selectedVenue.id}/time-slots`, {
          params: {
            date: this.searchForm.date
          },
          headers: {
            'Content-Type': 'application/json'
          }
        })
        this.timeSlots = response.data
      } catch (error) {
        this.$message.error('加载时间段信息失败')
        console.error('加载时间段信息失败:', error)
      }
    },
    selectTimeSlot(timeSlot) {
      this.selectedTimeSlot = timeSlot
    },
    calculateEstimatedCost() {
      if (!this.selectedTimeSlot) return 0
      return this.selectedTimeSlot.price
    },
    async confirmBooking() {
      if (!this.selectedTimeSlot) return
      
      try {
        const bookingData = {
          venueId: this.selectedVenue.id,
          date: this.searchForm.date,
          startTime: this.selectedTimeSlot.startTime,
          endTime: this.selectedTimeSlot.endTime,
          remarks: this.bookingForm.remarks
        }
        
        await axios.post('/api/bookings', bookingData, {
          headers: {
            'Content-Type': 'application/json'
          }
        })
        this.$message.success('预约成功')
        this.timeSlotDialogVisible = false
        this.loadVenues()
      } catch (error) {
        this.$message.error('预约失败')
        console.error('预约失败:', error)
      }
    },
    showVenueDetails(venue) {
      this.selectedVenue = venue
      this.venueDetailDialogVisible = true
    }
  }
}
</script>

<style scoped>
.venue-booking-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.time-slots-container {
  margin: 20px 0;
}

.time-slot {
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.time-slot.available {
  background-color: #f0f9eb;
  border-color: #67c23a;
}

.time-slot.booked {
  background-color: #fef0f0;
  border-color: #f56c6c;
  cursor: not-allowed;
}

.time-slot.special {
  background-color: #fdf6ec;
  border-color: #e6a23c;
}

.time-slot.selected {
  background-color: #409eff;
  color: white;
}

.time-slot-price {
  margin-top: 5px;
  font-size: 12px;
  color: #666;
}

.booking-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.venue-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.venue-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.venue-info {
  line-height: 1.8;
}
</style> 