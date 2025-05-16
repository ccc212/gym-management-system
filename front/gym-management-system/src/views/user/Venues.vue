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

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 数据定义
const venues = ref([])
const loading = ref(false)
const searchForm = reactive({
    venueType: '',
    date: getTodayDate()
})
const venueTypes = [
    { value: 'basketball', label: '篮球场' },
    { value: 'football', label: '足球场' },
    { value: 'badminton', label: '羽毛球场' },
    { value: 'tennis', label: '网球场' },
    { value: 'swimming', label: '游泳池' },
    { value: 'table_tennis', label: '乒乓球室' }
]
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})
const venueList = ref([])
const datePickerOptions = {
    disabledDate(time) {
        return time.getTime() < Date.now() - 8.64e7;
    }
}
const timeSlotDialogVisible = ref(false)
const selectedVenue = ref(null)
const timeSlots = ref([])
const selectedTimeSlot = ref(null)
const bookingForm = reactive({
    remarks: ''
})
const bookingRules = {
    remarks: [
        { required: false, message: '请输入预约备注信息', trigger: 'blur' }
    ]
}
const venueDetailDialogVisible = ref(false)
const apiBaseUrl = '/api/venues'

// 工具函数
function getTodayDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = (today.getMonth() + 1).toString().padStart(2, '0');
    const day = today.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
}
function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
    if (!(date instanceof Date) || isNaN(date)) {
        return '';
    }
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 加载场地数据
async function loadVenues() {
    loading.value = true;
    try {
        const params = {
            page: pagination.currentPage,
            size: pagination.pageSize
        };
        if (searchForm.venueType) {
            params.type = searchForm.venueType;
        }
        const response = await axios.get('/api/venues', { params });
        if (response.data.code === 200) {
            const pageData = response.data.data;
            venueList.value = pageData.records || [];
            // 转换场地类型为中文显示
            venueList.value.forEach(venue => {
                if (venue.type) {
                    const typeOption = venueTypes.find(t => t.value === venue.type);
                    if (typeOption) {
                        venue.type = typeOption.label;
                    }
                }
            });
            pagination.total = pageData.total || 0;
        } else {
            ElMessage.error(response.data.message || '加载场地数据失败');
        }
    } catch (error) {
        console.error('加载场地数据失败:', error);
        ElMessage.error('加载场地数据失败');
    } finally {
        loading.value = false;
    }
}

// 搜索场地
function searchVenues() {
    pagination.currentPage = 1;
    loadVenues();
}

// 重置搜索条件
function resetSearch() {
    searchForm.venueType = '';
    searchForm.date = '';
    searchVenues();
}

// 分页大小变化处理
function handleSizeChange(val) {
    pagination.pageSize = val;
    loadVenues();
}

// 页码变化处理
function handleCurrentChange(val) {
    pagination.currentPage = val;
    loadVenues();
}

// 显示时间段选择弹窗
function showTimeSlots(venue) {
    if (!searchForm.date) {
        ElMessage.warning('请先选择日期');
        return;
    }
    selectedVenue.value = venue;
    selectedTimeSlot.value = null;
    bookingForm.remarks = '';
    loadTimeSlots();
    timeSlotDialogVisible.value = true;
}

// 加载时间段数据
async function loadTimeSlots() {
    try {
        const date = searchForm.date;
        if (!date) {
            ElMessage.warning('请先选择日期');
            return;
        }
        const response = await axios.get(`${apiBaseUrl}/${selectedVenue.value.id}/time-slots`, {
            params: { date: date }
        });
        if (response.data.code === 200 && Array.isArray(response.data.data)) {
            timeSlots.value = response.data.data.map(slot => {
                const startTime = typeof slot.startTime === 'string' ? slot.startTime : 
                               (slot.startTime.format ? slot.startTime.format('HH:mm') : '00:00');
                const endTime = typeof slot.endTime === 'string' ? slot.endTime : 
                             (slot.endTime.format ? slot.endTime.format('HH:mm') : '00:00');
                return {
                    ...slot,
                    startTime: startTime,
                    endTime: endTime,
                    price: Number(slot.price || selectedVenue.value.pricePerHour).toFixed(2),
                    status: slot.status || 'AVAILABLE'
                };
            });
        } else {
            timeSlots.value = [];
            ElMessage.warning(response.data.msg || '没有可用的时间段');
        }
    } catch (error) {
        console.error('加载时间段失败:', error);
        ElMessage.error('加载时间段失败: ' + (error.response?.data?.msg || error.message));
        timeSlots.value = [];
    }
}

// 选择时间段
function selectTimeSlot(timeSlot) {
    if (timeSlot.status === 'AVAILABLE') {
        selectedTimeSlot.value = {
            ...timeSlot,
            startTime: timeSlot.startTime,
            endTime: timeSlot.endTime,
            price: Number(timeSlot.price || selectedVenue.value.pricePerHour).toFixed(2)
        };
    }
}

// 计算预估费用
function calculateEstimatedCost() {
    if (selectedTimeSlot.value) {
        return selectedTimeSlot.value.price;
    }
    return 0;
}

// 确认预约
function confirmBooking() {
    if (!selectedTimeSlot.value) {
        ElMessage.warning('请选择预约时间段');
        return;
    }
    // 表单校验
    // 获取当前用户
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const userId = currentUser && currentUser.id ? currentUser.id : null;
    const cardNumber = currentUser && currentUser.cardNumber ? currentUser.cardNumber : '12345678';
    if (!userId) {
        ElMessage.error('请先登录');
        return;
    }
    // 计算时间差（小时）
    const startTime = selectedTimeSlot.value.startTime;
    const endTime = selectedTimeSlot.value.endTime;
    const [startHour, startMinute] = startTime.split(':').map(Number);
    const [endHour, endMinute] = endTime.split(':').map(Number);
    const duration = (endHour - startHour) + (endMinute - startMinute) / 60;
    // 构建预约请求数据
    const bookingData = {
        venueId: selectedVenue.value.id,
        userId: userId,
        cardNumber: cardNumber,
        startTime: `${searchForm.date}T${startTime}:00`,
        endTime: `${searchForm.date}T${endTime}:00`,
        numberOfPeople: 1,
        remarks: bookingForm.remarks || '',
        status: 'PENDING',
        cost: Number((selectedVenue.value.pricePerHour * duration).toFixed(2)),
        duration: duration
    };
    axios.post(`${apiBaseUrl}/${selectedVenue.value.id}/reserve`, bookingData)
        .then(response => {
            if (response.data.code === 200) {
                ElMessage.success('预约成功');
                timeSlotDialogVisible.value = false;
                loadVenues();
            } else {
                ElMessage.error(response.data.msg || '预约失败');
            }
        })
        .catch(error => {
            console.error('预约失败:', error);
            ElMessage.error('预约失败，请稍后重试');
        });
}

// 显示场馆详情
function showVenueDetails(venue) {
    selectedVenue.value = venue;
    venueDetailDialogVisible.value = true;
}

// 生命周期
onMounted(() => {
    loadVenues();
});
</script>

<style scoped lang="scss">
.venue-booking-container {
    padding: 20px;
    .search-form {
        margin-bottom: 20px;
    }
    .pagination-container {
        margin-top: 20px;
        text-align: right;
    }
    .time-slots-container {
        margin: 20px 0;
        .time-slot {
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            &:hover {
                opacity: 0.8;
            }
            &.available {
                background-color: #67c23a;
                color: white;
            }
            &.booked {
                background-color: #e6a23c;
                color: white;
            }
            &.special {
                background-color: #909399;
                color: white;
            }
            &.selected {
                border: 2px solid #409eff;
            }
            .time-slot-price {
                margin-top: 5px;
                font-size: 12px;
            }
        }
    }
    .booking-summary {
        margin-top: 20px;
        padding: 10px;
        background-color: #f5f7fa;
        border-radius: 4px;
        p {
            margin: 5px 0;
        }
    }
    .venue-detail {
        .venue-image {
            text-align: center;
            margin-bottom: 20px;
            img {
                max-width: 100%;
                max-height: 200px;
                object-fit: cover;
            }
        }
        .venue-info {
            h3 {
                margin-top: 0;
                margin-bottom: 20px;
                text-align: center;
            }
            p {
                margin: 10px 0;
            }
        }
    }
}
</style> 