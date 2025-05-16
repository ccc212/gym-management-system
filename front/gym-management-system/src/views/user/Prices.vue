<template>
    <div class="prices-container">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <span>价格查询</span>
                    <div class="header-right">
                        <el-select v-model="venueType" placeholder="场馆类型" @change="handleSearch">
                            <el-option label="全部" value="" />
                            <el-option label="篮球场" value="BASKETBALL" />
                            <el-option label="羽毛球场" value="BADMINTON" />
                            <el-option label="网球场" value="TENNIS" />
                            <el-option label="乒乓球场" value="TABLE_TENNIS" />
                            <el-option label="游泳馆" value="SWIMMING" />
                        </el-select>
                    </div>
                </div>
            </template>

            <div class="prices-content" v-loading="loading">
                <el-row :gutter="20">
                    <el-col :span="8" v-for="venue in venues" :key="venue.id">
                        <el-card class="venue-card" :body-style="{ padding: '0px' }">
                            <img :src="venue.image" class="venue-image" />
                            <div class="venue-info">
                                <h3>{{ venue.name }}</h3>
                                <p class="venue-type">{{ getVenueTypeName(venue.type) }}</p>
                                <div class="price-info">
                                    <div class="price-item">
                                        <span class="label">工作日价格：</span>
                                        <span class="value">¥{{ venue.weekdayPrice }}/小时</span>
                                    </div>
                                    <div class="price-item">
                                        <span class="label">周末价格：</span>
                                        <span class="value">¥{{ venue.weekendPrice }}/小时</span>
                                    </div>
                                    <div class="price-item">
                                        <span class="label">节假日价格：</span>
                                        <span class="value">¥{{ venue.holidayPrice }}/小时</span>
                                    </div>
                                </div>
                                <div class="venue-description">
                                    <p>{{ venue.description }}</p>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>

                <div class="pagination-container">
                    <el-pagination
                        v-model:current-page="currentPage"
                        v-model:page-size="pageSize"
                        :page-sizes="[6, 12, 24, 36]"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="total"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                    />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getVenues } from '@/api/venue'

// 场馆类型筛选
const venueType = ref('')

// 加载状态
const loading = ref(false)

// 表格数据
const venues = ref([])
const currentPage = ref(1)
const pageSize = ref(6)
const total = ref(0)

// 获取场馆类型名称
const getVenueTypeName = (type: string) => {
    const typeMap: { [key: string]: string } = {
        'BASKETBALL': '篮球场',
        'BADMINTON': '羽毛球场',
        'TENNIS': '网球场',
        'TABLE_TENNIS': '乒乓球场',
        'SWIMMING': '游泳馆'
    }
    return typeMap[type] || type
}

// 加载场馆列表
const loadVenues = async () => {
    loading.value = true
    try {
        const res = await getVenues({
            page: currentPage.value,
            size: pageSize.value,
            type: venueType.value
        })
        venues.value = res.data.list
        total.value = res.data.total
    } catch (error) {
        console.error('加载场馆列表失败:', error)
        ElMessage.error('加载场馆列表失败')
    } finally {
        loading.value = false
    }
}

// 处理搜索
const handleSearch = () => {
    currentPage.value = 1
    loadVenues()
}

// 处理分页
const handleSizeChange = (val: number) => {
    pageSize.value = val
    loadVenues()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    loadVenues()
}

onMounted(() => {
    loadVenues()
})
</script>

<style scoped lang="scss">
.prices-container {
    padding: 20px;

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .header-right {
            display: flex;
            gap: 16px;
        }
    }

    .prices-content {
        margin-top: 20px;

        .venue-card {
            margin-bottom: 20px;
            transition: all 0.3s;

            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
            }

            .venue-image {
                width: 100%;
                height: 200px;
                object-fit: cover;
            }

            .venue-info {
                padding: 14px;

                h3 {
                    margin: 0;
                    font-size: 16px;
                    color: #303133;
                }

                .venue-type {
                    color: #909399;
                    font-size: 14px;
                    margin: 8px 0;
                }

                .price-info {
                    margin: 12px 0;
                    padding: 12px;
                    background-color: #f5f7fa;
                    border-radius: 4px;

                    .price-item {
                        display: flex;
                        justify-content: space-between;
                        margin-bottom: 8px;

                        &:last-child {
                            margin-bottom: 0;
                        }

                        .label {
                            color: #606266;
                        }

                        .value {
                            color: #f56c6c;
                            font-weight: bold;
                        }
                    }
                }

                .venue-description {
                    color: #606266;
                    font-size: 14px;
                    margin-top: 12px;
                    height: 40px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    -webkit-box-orient: vertical;
                }
            }
        }

        .pagination-container {
            margin-top: 20px;
            display: flex;
            justify-content: flex-end;
        }
    }
}
</style> 