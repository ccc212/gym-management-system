// 收费标准组件
const UserPricesComponent = {
    template: `
        <div class="prices-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>收费标准</h2>
                </div>
                
                <!-- 说明文字 -->
                <div class="description">
                    <p>以下是我们体育场馆的各项收费标准，请在预约前仔细阅读。如有疑问，请联系场馆管理员。</p>
                </div>
                
                <!-- 选项卡 -->
                <el-tabs v-model="activeTab">
                    <el-tab-pane label="基本收费" name="basic">
                        <div class="table-container">
                            <h3>场地基本收费标准</h3>
                            <el-table
                                :data="basicPrices"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="venueType" label="场地类型" width="150"></el-table-column>
                                <el-table-column prop="unit" label="收费单位" width="120"></el-table-column>
                                <el-table-column prop="regularPrice" label="非高峰时段" width="120">
                                    <template slot-scope="scope">
                                        {{ scope.row.regularPrice }} 元
                                    </template>
                                </el-table-column>
                                <el-table-column prop="peakPrice" label="高峰时段" width="120">
                                    <template slot-scope="scope">
                                        {{ scope.row.peakPrice }} 元
                                    </template>
                                </el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                            
                            <div class="note-box">
                                <h4>高峰时段说明：</h4>
                                <p>工作日：18:00-21:00</p>
                                <p>周末及节假日：9:00-21:00</p>
                            </div>
                        </div>
                    </el-tab-pane>
                    
                    <el-tab-pane label="优惠政策" name="discount">
                        <div class="discount-list">
                            <h3>会员优惠</h3>
                            <el-table
                                :data="memberDiscounts"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="level" label="会员等级" width="150"></el-table-column>
                                <el-table-column prop="discount" label="折扣" width="120"></el-table-column>
                                <el-table-column prop="condition" label="条件" width="200"></el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                            
                            <h3 class="mt-20">团体优惠</h3>
                            <el-table
                                :data="groupDiscounts"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="type" label="团体类型" width="150"></el-table-column>
                                <el-table-column prop="discount" label="折扣" width="120"></el-table-column>
                                <el-table-column prop="condition" label="条件" width="200"></el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                            
                            <h3 class="mt-20">特殊人群优惠</h3>
                            <el-table
                                :data="specialDiscounts"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="type" label="人群类型" width="150"></el-table-column>
                                <el-table-column prop="discount" label="优惠幅度" width="120"></el-table-column>
                                <el-table-column prop="condition" label="证明文件" width="200"></el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                        </div>
                    </el-tab-pane>
                    
                    <el-tab-pane label="设备租赁" name="equipment">
                        <div class="equipment-list">
                            <h3>运动设备租赁价格</h3>
                            <el-table
                                :data="equipmentRentals"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="name" label="设备名称" width="150"></el-table-column>
                                <el-table-column prop="price" label="租赁价格" width="120">
                                    <template slot-scope="scope">
                                        {{ scope.row.price }} 元
                                    </template>
                                </el-table-column>
                                <el-table-column prop="unit" label="计费单位" width="120"></el-table-column>
                                <el-table-column prop="deposit" label="押金" width="120">
                                    <template slot-scope="scope">
                                        {{ scope.row.deposit }} 元
                                    </template>
                                </el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                        </div>
                    </el-tab-pane>
                    
                    <el-tab-pane label="其他费用" name="other">
                        <div class="other-fees">
                            <h3>其他相关费用</h3>
                            <el-table
                                :data="otherFees"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="name" label="收费项目" width="200"></el-table-column>
                                <el-table-column prop="price" label="收费标准" width="150">
                                    <template slot-scope="scope">
                                        {{ scope.row.price }} 元
                                    </template>
                                </el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                            
                            <div class="note-box mt-20">
                                <h4>注意事项：</h4>
                                <ul>
                                    <li>所有费用均可使用场馆电子钱包、微信或支付宝支付</li>
                                    <li>包月或包年会员请到前台办理会员卡</li>
                                    <li>团体预订请提前3天联系场馆管理员</li>
                                    <li>取消预约需提前4小时，否则将收取违约金</li>
                                    <li>如遇特殊节假日，收费标准可能会有调整，请以场馆公告为准</li>
                                </ul>
                            </div>
                        </div>
                    </el-tab-pane>
                    
                    <el-tab-pane label="违约规则" name="violation">
                        <div class="violation-rules">
                            <h3>预约违约规则</h3>
                            <el-table
                                :data="violationRules"
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
                                border>
                                <el-table-column prop="type" label="违约类型" width="200"></el-table-column>
                                <el-table-column prop="penalty" label="处罚措施" width="200"></el-table-column>
                                <el-table-column prop="description" label="说明"></el-table-column>
                            </el-table>
                            
                            <div class="note-box mt-20">
                                <h4>友情提示：</h4>
                                <p>为了维护良好的预约秩序，请遵守预约规则，如确实无法按时使用场地，请提前取消预约。</p>
                                <p>连续违约3次将被限制预约功能15天。</p>
                            </div>
                        </div>
                    </el-tab-pane>
                </el-tabs>
                
                <!-- 联系方式 -->
                <div class="contact-info">
                    <h3>联系我们</h3>
                    <p><i class="el-icon-phone"></i> 咨询电话：123-4567-8900</p>
                    <p><i class="el-icon-message"></i> 电子邮箱：service@gym.example.com</p>
                    <p><i class="el-icon-location-information"></i> 场馆地址：示例市示例区示例路123号</p>
                    <p><i class="el-icon-time"></i> 开放时间：每天 08:00-22:00</p>
                </div>
            </el-card>
        </div>
    `,
    data() {
        return {
            activeTab: 'basic',
            // 基本收费
            basicPrices: [
                { venueType: '篮球场', unit: '小时', regularPrice: 80, peakPrice: 120, description: '标准篮球场，可容纳5v5比赛' },
                { venueType: '足球场', unit: '小时', regularPrice: 200, peakPrice: 300, description: '7人制足球场，人造草皮' },
                { venueType: '羽毛球场', unit: '小时', regularPrice: 40, peakPrice: 60, description: '标准羽毛球场地，含照明' },
                { venueType: '网球场', unit: '小时', regularPrice: 60, peakPrice: 90, description: '室外硬地网球场' },
                { venueType: '游泳池', unit: '小时/人', regularPrice: 30, peakPrice: 40, description: '25米标准泳道，含淋浴' },
                { venueType: '乒乓球室', unit: '小时/台', regularPrice: 20, peakPrice: 30, description: '标准乒乓球台，含球拍' }
            ],
            // 会员优惠
            memberDiscounts: [
                { level: '普通会员', discount: '9.5折', condition: '注册会员', description: '适用于所有场地预约' },
                { level: '银卡会员', discount: '9折', condition: '累计消费满1000元', description: '适用于所有场地预约' },
                { level: '金卡会员', discount: '8.5折', condition: '累计消费满3000元', description: '适用于所有场地预约' },
                { level: '铂金会员', discount: '8折', condition: '累计消费满5000元', description: '适用于所有场地预约，非高峰时段可享7.5折' }
            ],
            // 团体优惠
            groupDiscounts: [
                { type: '学校团体', discount: '8折', condition: '10人以上', description: '需提供学校证明' },
                { type: '企业团体', discount: '8.5折', condition: '15人以上', description: '需提前3天预约' },
                { type: '社区团体', discount: '8.5折', condition: '12人以上', description: '需提供社区证明' }
            ],
            // 特殊人群优惠
            specialDiscounts: [
                { type: '学生', discount: '8折', condition: '学生证', description: '限年龄25岁以下在校学生' },
                { type: '老年人', discount: '7折', condition: '老年证/身份证', description: '限年龄60岁以上老人' },
                { type: '军人/警察', discount: '8折', condition: '军官证/警官证', description: '现役军人及警务人员' },
                { type: '残障人士', discount: '5折', condition: '残疾证', description: '所有场地适用' }
            ],
            // 设备租赁
            equipmentRentals: [
                { name: '篮球', price: 10, unit: '次', deposit: 50, description: '标准7号球' },
                { name: '足球', price: 15, unit: '次', deposit: 50, description: '5号标准比赛球' },
                { name: '羽毛球拍', price: 10, unit: '小时/副', deposit: 100, description: '含2只羽毛球' },
                { name: '羽毛球', price: 5, unit: '只', deposit: 0, description: '耐打比赛用球' },
                { name: '网球拍', price: 15, unit: '小时/副', deposit: 150, description: '含2只网球' },
                { name: '乒乓球拍', price: 5, unit: '小时/副', deposit: 50, description: '含1只乒乓球' },
                { name: '游泳帽', price: 5, unit: '次', deposit: 20, description: '一次性使用' },
                { name: '泳镜', price: 10, unit: '次', deposit: 50, description: '防雾泳镜' }
            ],
            // 其他费用
            otherFees: [
                { name: '储物柜', price: 5, description: '每次使用，使用完毕请及时取走物品' },
                { name: '淋浴', price: 5, description: '每人次，羽毛球、篮球、足球场地预约包含' },
                { name: '私教', price: 100, description: '每小时，需提前预约' },
                { name: '场地照明', price: 20, description: '每小时，特殊光线需求' },
                { name: '停车', price: 5, description: '每小时，场馆用户前2小时免费' }
            ],
            // 违约规则
            violationRules: [
                { type: '迟到15-30分钟', penalty: '收取50%费用', description: '超过预约时间15分钟未签到，视为迟到' },
                { type: '迟到30分钟以上', penalty: '收取全部费用', description: '超过预约时间30分钟未签到，视为爽约' },
                { type: '取消预约(4小时前)', penalty: '免费取消', description: '提前4小时以上取消预约不收取费用' },
                { type: '取消预约(4小时内)', penalty: '收取30%费用', description: '提前不足4小时取消预约收取部分费用' },
                { type: '未取消直接爽约', penalty: '收取全部费用', description: '未提前取消且未按时到场使用，收取全部费用' },
                { type: '连续3次违约', penalty: '限制预约15天', description: '一个月内连续3次违约，将限制预约功能15天' }
            ]
        };
    }
};