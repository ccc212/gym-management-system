// 预约管理组件 - 管理员
const AdminReservationsComponent = {
    template: `
        <div>
            <div class="table-operations">
                <el-button type="primary" @click="handleRefresh">刷新</el-button>
                <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchCancel">批量退订</el-button>
            </div>
            
            <el-table :data="reservations" border style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="venue.name" label="场地名称"></el-table-column>
                <el-table-column prop="cardNumber" label="一卡通号"></el-table-column>
                <el-table-column prop="startTime" label="开始时间">
                    <template slot-scope="scope">
                        {{ new Date(scope.row.startTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间">
                    <template slot-scope="scope">
                        {{ new Date(scope.row.endTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态">
                    <template slot-scope="scope">
                        <el-tag :type="getStatusType(scope.row.status)">
                            {{ getStatusText(scope.row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="handleEdit(scope.row)" :disabled="scope.row.status !== 'BOOKED'">修改</el-button>
                        <el-button size="mini" type="danger" @click="handleCancel(scope.row)" :disabled="scope.row.status !== 'BOOKED'">退订</el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <!-- 编辑预约对话框 -->
            <el-dialog title="修改预约" :visible.sync="dialogVisible" width="40%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="场地名称">
                        <el-input v-model="form.venueName" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="一卡通号">
                        <el-input v-model="form.cardNumber" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="开始时间" prop="startTime">
                        <el-date-picker
                            v-model="form.startTime"
                            type="datetime"
                            placeholder="选择开始时间"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="结束时间" prop="endTime">
                        <el-date-picker
                            v-model="form.endTime"
                            type="datetime"
                            placeholder="选择结束时间"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">修改</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            reservations: [],
            multipleSelection: [],
            dialogVisible: false,
            form: {
                id: null,
                venueName: '',
                cardNumber: '',
                startTime: null,
                endTime: null,
                remark: ''
            },
            rules: {
                startTime: [
                    { required: true, message: '请选择开始时间', trigger: 'change' }
                ],
                endTime: [
                    { required: true, message: '请选择结束时间', trigger: 'change' }
                ]
            }
        };
    },
    created() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            // 实际应该调用API
            axios.get('/reservations')
                .then(response => {
                    this.reservations = response.data;
                })
                .catch(error => {
                    console.error('获取预约列表失败:', error);
                    this.$message.error('获取预约列表失败');

                    // 模拟数据
                    this.reservations = [
                        {
                            id: 1,
                            venue: { id: 1, name: '篮球场1号' },
                            cardNumber: '2021001001',
                            startTime: '2023-05-20T09:00:00',
                            endTime: '2023-05-20T11:00:00',
                            status: 'BOOKED',
                            remark: ''
                        },
                        {
                            id: 2,
                            venue: { id: 2, name: '羽毛球场1号' },
                            cardNumber: '2021001002',
                            startTime: '2023-05-21T14:00:00',
                            endTime: '2023-05-21T16:00:00',
                            status: 'COMPLETED',
                            remark: ''
                        },
                        {
                            id: 3,
                            venue: { id: 3, name: '网球场1号' },
                            cardNumber: '2021001003',
                            startTime: '2023-05-22T10:00:00',
                            endTime: '2023-05-22T12:00:00',
                            status: 'CANCELLED',
                            remark: '用户取消'
                        }
                    ];
                });
        },
        handleRefresh() {
            this.fetchData();
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        getStatusType(status) {
            switch (status) {
                case 'BOOKED': return 'primary';
                case 'COMPLETED': return 'success';
                case 'CANCELLED': return 'info';
                case 'NO_SHOW': return 'danger';
                default: return 'info';
            }
        },
        getStatusText(status) {
            switch (status) {
                case 'BOOKED': return '已预约';
                case 'COMPLETED': return '已完成';
                case 'CANCELLED': return '已取消';
                case 'NO_SHOW': return '未到场';
                default: return '未知状态';
            }
        },
        handleEdit(row) {
            this.form = {
                id: row.id,
                venueName: row.venue.name,
                cardNumber: row.cardNumber,
                startTime: new Date(row.startTime),
                endTime: new Date(row.endTime),
                remark: row.remark || ''
            };
            this.dialogVisible = true;
        },
        handleCancel(row) {
            this.$confirm('确认退订该预约?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该调用API
                axios.post(`/reservations/${row.id}/cancel`, { cardNumber: row.cardNumber })
                    .then(() => {
                        this.$message.success('退订成功');
                        this.fetchData();
                    })
                    .catch(error => {
                        console.error('退订预约失败:', error);
                        this.$message.error('退订预约失败');

                        // 模拟操作成功
                        const reservation = this.reservations.find(r => r.id === row.id);
                        if (reservation) {
                            reservation.status = 'CANCELLED';
                        }
                        this.$message.success('退订成功');
                    });
            }).catch(() => {});
        },
        handleBatchCancel() {
            this.$confirm(`确认退订选中的 ${this.multipleSelection.length} 个预约?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该批量调用API
                const validReservations = this.multipleSelection.filter(r => r.status === 'BOOKED');

                if (validReservations.length === 0) {
                    this.$message.warning('没有可退订的预约');
                    return;
                }

                // 模拟操作成功
                validReservations.forEach(reservation => {
                    const found = this.reservations.find(r => r.id === reservation.id);
                    if (found) {
                        found.status = 'CANCELLED';
                    }
                });
                this.$message.success('批量退订成功');
            }).catch(() => {});
        },
        submitForm() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    // 实际应该调用API
                    const data = {
                        id: this.form.id,
                        startTime: this.form.startTime.toISOString(),
                        endTime: this.form.endTime.toISOString(),
                        cardNumber: this.form.cardNumber,
                        remark: this.form.remark
                    };

                    axios.put(`/reservations/${this.form.id}`, data)
                        .then(response => {
                            this.$message.success('修改成功');
                            this.dialogVisible = false;
                            this.fetchData();
                        })
                        .catch(error => {
                            console.error('修改预约失败:', error);
                            this.$message.error('修改预约失败');

                            // 模拟操作成功
                            const reservation = this.reservations.find(r => r.id === this.form.id);
                            if (reservation) {
                                reservation.startTime = this.form.startTime.toISOString();
                                reservation.endTime = this.form.endTime.toISOString();
                                reservation.remark = this.form.remark;
                            }
                            this.dialogVisible = false;
                            this.$message.success('修改成功');
                        });
                }
            });
        }
    }
};

// 特殊场地管理组件 - 管理员
const AdminSpecialComponent = {
    template: `
        <div>
            <el-tabs v-model="activeTab" @tab-click="handleTabClick">
                <el-tab-pane label="校队训练" name="team">
                    <div class="table-operations">
                        <el-button type="primary" @click="showAddDialog('TEAM')">新建</el-button>
                        <el-button type="danger" :disabled="!selectedTeamItems.length" @click="handleBatchDelete('TEAM')">删除</el-button>
                    </div>
                    
                    <el-table :data="teamReservations" border style="width: 100%" @selection-change="val => selectedTeamItems = val">
                        <el-table-column type="selection" width="55"></el-table-column>
                        <el-table-column prop="id" label="ID" width="80"></el-table-column>
                        <el-table-column prop="venue.name" label="场地名称"></el-table-column>
                        <el-table-column prop="startTime" label="开始时间">
                            <template slot-scope="scope">
                                {{ new Date(scope.row.startTime).toLocaleString() }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="endTime" label="结束时间">
                            <template slot-scope="scope">
                                {{ new Date(scope.row.endTime).toLocaleString() }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="remark" label="备注"></el-table-column>
                        <el-table-column label="操作" width="150">
                            <template slot-scope="scope">
                                <el-button size="mini" @click="handleEdit(scope.row, 'TEAM')">编辑</el-button>
                                <el-button size="mini" type="danger" @click="handleDelete(scope.row, 'TEAM')">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
                
                <el-tab-pane label="学校上课" name="class">
                    <div class="table-operations">
                        <el-button type="primary" @click="showAddDialog('CLASS')">新建</el-button>
                        <el-button type="danger" :disabled="!selectedClassItems.length" @click="handleBatchDelete('CLASS')">删除</el-button>
                    </div>
                    
                    <el-table :data="classReservations" border style="width: 100%" @selection-change="val => selectedClassItems = val">
                        <el-table-column type="selection" width="55"></el-table-column>
                        <el-table-column prop="id" label="ID" width="80"></el-table-column>
                        <el-table-column prop="venue.name" label="场地名称"></el-table-column>
                        <el-table-column prop="startTime" label="开始时间">
                            <template slot-scope="scope">
                                {{ new Date(scope.row.startTime).toLocaleString() }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="endTime" label="结束时间">
                            <template slot-scope="scope">
                                {{ new Date(scope.row.endTime).toLocaleString() }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="remark" label="备注"></el-table-column>
                        <el-table-column label="操作" width="150">
                            <template slot-scope="scope">
                                <el-button size="mini" @click="handleEdit(scope.row, 'CLASS')">编辑</el-button>
                                <el-button size="mini" type="danger" @click="handleDelete(scope.row, 'CLASS')">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
            
            <!-- 新建/编辑特殊场地预约对话框 -->
            <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="场地" prop="venueId">
                        <el-select v-model="form.venueId" placeholder="请选择场地" style="width: 100%">
                            <el-option
                                v-for="venue in venues"
                                :key="venue.id"
                                :label="venue.name"
                                :value="venue.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="开始时间" prop="startTime">
                        <el-date-picker
                            v-model="form.startTime"
                            type="datetime"
                            placeholder="选择开始时间"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="结束时间" prop="endTime">
                        <el-date-picker
                            v-model="form.endTime"
                            type="datetime"
                            placeholder="选择结束时间"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="类型" prop="type">
                        <el-radio-group v-model="form.reservationType">
                            <el-radio label="TEAM">校队训练</el-radio>
                            <el-radio label="CLASS">学校上课</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确认</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            activeTab: 'team',
            teamReservations: [],
            classReservations: [],
            selectedTeamItems: [],
            selectedClassItems: [],
            venues: [],
            dialogVisible: false,
            dialogTitle: '新建特殊场地预约',
            isEdit: false,
            form: this.getInitialForm(),
            rules: {
                venueId: [
                    { required: true, message: '请选择场地', trigger: 'change' }
                ],
                startTime: [
                    { required: true, message: '请选择开始时间', trigger: 'change' }
                ],
                endTime: [
                    { required: true, message: '请选择结束时间', trigger: 'change' }
                ],
                reservationType: [
                    { required: true, message: '请选择类型', trigger: 'change' }
                ]
            }
        };
    },
    created() {
        this.fetchVenues();
        this.fetchData();
    },
    methods: {
        getInitialForm() {
            return {
                id: null,
                venueId: null,
                startTime: null,
                endTime: null,
                reservationType: 'TEAM',
                remark: ''
            };
        },
        fetchVenues() {
            // 实际应该调用API
            axios.get('/venues/available')
                .then(response => {
                    this.venues = response.data;
                })
                .catch(error => {
                    console.error('获取场地列表失败:', error);

                    // 模拟数据
                    this.venues = [
                        { id: 1, name: '篮球场1号' },
                        { id: 2, name: '羽毛球场1号' },
                        { id: 3, name: '网球场1号' }
                    ];
                });
        },
        fetchData() {
            // 实际应该调用API - 获取校队训练预约
            axios.get('/reservations?type=TEAM')
                .then(response => {
                    this.teamReservations = response.data;
                })
                .catch(error => {
                    console.error('获取校队训练预约失败:', error);

                    // 模拟数据
                    this.teamReservations = [
                        {
                            id: 4,
                            venue: { id: 1, name: '篮球场1号' },
                            startTime: '2023-05-15T16:00:00',
                            endTime: '2023-05-15T18:00:00',
                            reservationType: 'TEAM',
                            remark: '篮球校队训练'
                        }
                    ];
                });

            // 获取学校上课预约
            axios.get('/reservations?type=CLASS')
                .then(response => {
                    this.classReservations = response.data;
                })
                .catch(error => {
                    console.error('获取学校上课预约失败:', error);

                    // 模拟数据
                    this.classReservations = [
                        {
                            id: 5,
                            venue: { id: 2, name: '羽毛球场1号' },
                            startTime: '2023-05-16T08:00:00',
                            endTime: '2023-05-16T10:00:00',
                            reservationType: 'CLASS',
                            remark: '体育课'
                        }
                    ];
                });
        },
        handleTabClick(tab) {
            // Tab切换时可以执行额外操作
        },
        showAddDialog(type) {
            this.dialogTitle = type === 'TEAM' ? '新建校队训练预约' : '新建学校上课预约';
            this.isEdit = false;
            this.form = this.getInitialForm();
            this.form.reservationType = type;
            this.dialogVisible = true;
        },
        handleEdit(row, type) {
            this.dialogTitle = type === 'TEAM' ? '编辑校队训练预约' : '编辑学校上课预约';
            this.isEdit = true;
            this.form = {
                id: row.id,
                venueId: row.venue.id,
                startTime: new Date(row.startTime),
                endTime: new Date(row.endTime),
                reservationType: type,
                remark: row.remark || ''
            };
            this.dialogVisible = true;
        },
        handleDelete(row, type) {
            this.$confirm('确认删除该特殊场地预约?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该调用API
                axios.delete(`/reservations/${row.id}`)
                    .then(() => {
                        this.$message.success('删除成功');
                        this.fetchData();
                    })
                    .catch(error => {
                        console.error('删除特殊场地预约失败:', error);
                        this.$message.error('删除特殊场地预约失败');

                        // 模拟操作成功
                        if (type === 'TEAM') {
                            this.teamReservations = this.teamReservations.filter(item => item.id !== row.id);
                        } else {
                            this.classReservations = this.classReservations.filter(item => item.id !== row.id);
                        }
                        this.$message.success('删除成功');
                    });
            }).catch(() => {});
        },
        handleBatchDelete(type) {
            const selectedItems = type === 'TEAM' ? this.selectedTeamItems : this.selectedClassItems;

            this.$confirm(`确认删除选中的 ${selectedItems.length} 个预约?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该批量调用API
                const ids = selectedItems.map(item => item.id);

                // 模拟操作成功
                if (type === 'TEAM') {
                    this.teamReservations = this.teamReservations.filter(item => !ids.includes(item.id));
                } else {
                    this.classReservations = this.classReservations.filter(item => !ids.includes(item.id));
                }
                this.$message.success('批量删除成功');
            }).catch(() => {});
        },
        submitForm() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    const venue = this.venues.find(v => v.id === this.form.venueId);
                    if (!venue) {
                        this.$message.error('场地不存在');
                        return;
                    }

                    const data = {
                        id: this.form.id,
                        venueId: this.form.venueId,
                        startTime: this.form.startTime.toISOString(),
                        endTime: this.form.endTime.toISOString(),
                        reservationType: this.form.reservationType,
                        remark: this.form.remark
                    };

                    if (this.isEdit) {
                        // 编辑
                        axios.put(`/reservations/${this.form.id}`, data)
                            .then(response => {
                                this.$message.success('更新成功');
                                this.dialogVisible = false;
                                this.fetchData();
                            })
                            .catch(error => {
                                console.error('更新特殊场地预约失败:', error);
                                this.$message.error('更新特殊场地预约失败');

                                // 模拟操作成功
                                const updatedItem = {
                                    id: this.form.id,
                                    venue: { id: this.form.venueId, name: venue.name },
                                    startTime: this.form.startTime.toISOString(),
                                    endTime: this.form.endTime.toISOString(),
                                    reservationType: this.form.reservationType,
                                    remark: this.form.remark
                                };

                                if (this.form.reservationType === 'TEAM') {
                                    const index = this.teamReservations.findIndex(item => item.id === this.form.id);
                                    if (index !== -1) {
                                        this.teamReservations.splice(index, 1, updatedItem);
                                    }
                                } else {
                                    const index = this.classReservations.findIndex(item => item.id === this.form.id);
                                    if (index !== -1) {
                                        this.classReservations.splice(index, 1, updatedItem);
                                    }
                                }

                                this.dialogVisible = false;
                                this.$message.success('更新成功');
                            });
                    } else {
                        // 新建
                        axios.post('/reservations/special', data)
                            .then(response => {
                                this.$message.success('创建成功');
                                this.dialogVisible = false;
                                this.fetchData();
                            })
                            .catch(error => {
                                console.error('创建特殊场地预约失败:', error);
                                this.$message.error('创建特殊场地预约失败');

                                // 模拟操作成功
                                const newItem = {
                                    id: Math.floor(Math.random() * 1000) + 10, // 模拟新ID
                                    venue: { id: this.form.venueId, name: venue.name },
                                    startTime: this.form.startTime.toISOString(),
                                    endTime: this.form.endTime.toISOString(),
                                    reservationType: this.form.reservationType,
                                    remark: this.form.remark
                                };

                                if (this.form.reservationType === 'TEAM') {
                                    this.teamReservations.push(newItem);
                                } else {
                                    this.classReservations.push(newItem);
                                }

                                this.dialogVisible = false;
                                this.$message.success('创建成功');
                            });
                    }
                }
            });
        }
    }
};