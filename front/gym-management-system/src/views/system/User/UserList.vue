<template>
    <el-main>
       <el-form :model="searchParm" :inline="true" size="default" >
        <el-form-item>
            <el-input v-model="searchParm.userNumber" placeholder="请输入用户号码(学号或工号)"></el-input>
        </el-form-item>
        <el-form-item>
            <el-input v-model="searchParm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item style="width: 200px;">
            <el-select v-model="searchParm.userType" placeholder="请选择用户类型" clearable>
                <el-option
                v-for="item in userTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                ></el-option>
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-button icon="Search" @click="searchBtn">搜索</el-button>
            <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
            <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
        </el-form-item>
       </el-form>

       <!-- 表格 -->
       <el-table :height="tableHeight":data="tableList" border stripe>
        <el-table-column prop="id" label="序号"width="100px"></el-table-column>
        <el-table-column prop="userType" label="用户类型"width="150px"></el-table-column>
        <el-table-column prop="userNumber" label="学号或工号"width="150px"></el-table-column>
        <el-table-column prop="name" label="姓名"width="100px"></el-table-column>
        <el-table-column prop="sex" label="性别"width="100px"></el-table-column>
        <el-table-column prop="phone" label="手机"width="150px"></el-table-column>
        <el-table-column prop="age" label="年龄"width="100px"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200px"></el-table-column>
        <el-table-column label="操作" width="350px" >
            <template #default="scope">
                <div v-if="scope.row.userNumber !== 'admin'">
                <el-button type="warning" icon="Edit" size="small" @click="resetPasswordBtn(scope.row)">重置密码</el-button>
                <el-button type="primary" icon="Edit" size="small" @click="editBtn(scope.row)">编辑</el-button>
                <el-button type="danger" icon="Delete" size="small" @click="deleteBtn(scope.row.id)">删除</el-button>
                </div>
            </template>
        </el-table-column>
       </el-table>

       <!-- 分页 -->
        <el-pagination
          :current-page.sync="searchParm.currentPage"
          :page-sizes="[10, 20, 40]"
          :page-size="searchParm.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="searchParm.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />

       <!-- 新增角色对话框 -->
      <el-dialog title="新增用户" v-model="openAdd" width="500px" append-to-body>
      <el-form ref="addRef" :model="addModel" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="userType">
           <el-select v-model="addModel.userType" placeholder="请选择用户类型" clearable>
            <el-option
              v-for="item in userTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
           <el-select v-model="addModel.roleId" placeholder="请选择角色" clearable>
            <el-option
              v-for="item in Roleoptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="departId">
            <el-select v-model="addModel.departId" placeholder="请选择部门" clearable>
            <el-option
              v-for="item in Departmentoptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="sectionId" v-if="showsectionOption">
           <el-select v-model="addModel.sectionId" placeholder="请选择班级"  clearable>
            <el-option
              v-for="item in Sectionoptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="号码" prop="userNumber">
            <el-input v-model="addModel.userNumber" placeholder="请输入用户号码(学号或工号)" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
            <el-input v-model="addModel.name" placeholder="请输入用户姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
        <el-select v-model="addModel.sex" placeholder="请选择性别" clearable>
          <el-option
            v-for="item in sexOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
            <el-input v-model="addModel.age" placeholder="请输入用户年龄" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
            <el-input v-model="addModel.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
            <el-input v-model="addModel.email" placeholder="请输入用户邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFormAdd">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog title="编辑部门" v-model="openEdit" width="500px" append-to-body>
      <el-form ref="addRef" :model="addModel" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="userType">
           <el-select v-model="addModel.userType" placeholder="请选择用户类型" clearable>
            <el-option
              v-for="item in userTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
           <el-select v-model="addModel.roleId" placeholder="请选择角色" clearable>
            <el-option
              v-for="item in Roleoptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="departId">
            <el-select v-model="addModel.departId" placeholder="请选择部门" clearable>
            <el-option
              v-for="item in Departmentoptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="sectionId" v-if="showsectionOption">
           <el-select v-model="addModel.sectionId" placeholder="请选择班级"  clearable>
            <el-option
              v-for="item in Sectionoptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="号码" prop="userNumber">
            <el-input v-model="addModel.userNumber" placeholder="请输入用户号码(学号或工号)" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
            <el-input v-model="addModel.name" placeholder="请输入用户姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
        <el-select v-model="addModel.sex" placeholder="请选择性别" clearable>
          <el-option
            v-for="item in sexOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
            <el-input v-model="addModel.age" placeholder="请输入用户年龄" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
            <el-input v-model="addModel.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
            <el-input v-model="addModel.email" placeholder="请输入用户邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFormEdit">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    </el-main>
</template>

<script setup lang="ts">
import {reactive,ref,watch} from 'vue'
import type {FormInstance} from 'element-plus'
import {ElMessage} from 'element-plus'
import {addUser,getUserList,deleteUser,editUser,getRoleById,getDepartById,getSectionById,resetPassword} from '../../../api/system/user/index'
import type {User} from '../../../api/system/user/UserModel'
import { nextTick } from 'vue'
import useInstance from '../../../hooks/useInstance'
import {getRoleSelect} from '../../../api/system/role/index'
import {getDepartmentSelect} from '../../../api/system/department/index'
import {getSectionSelect} from '../../../api/system/section/index'


//获取全局
const {global} = useInstance()

//表单ref属性
const addRef = ref<FormInstance>()

//表单的对象
const searchParm = reactive({
    id:'',
    currentPage:1,
    pageSize:10,
    total:0,
    userNumber:'',
    name:'',
    userType:'',
})

//新增表单对象
const addModel = reactive({
    id:'',
    userNumber:'',
    password:'',
    name:'',
    sex:'',
    age:'',
    phone:'',
    email:'',
    roleId:'',
    departId:'',
    userType:'',
    sectionId:'',
})

//表单验证规则
const rules = reactive({
    userNumber:[
        {
            required: true,
            message: '请输入用户号码(学号或工号)',
            trigger: 'blur'
        }
    ],
    name:[
        {
            required: true,
            message: '请输入用户姓名',
            trigger: 'blur'
        }
    ],
    sex:[
        {
            required: true,
            message: '请选择性别',
            trigger: 'blur'
        }
    ],
    age:[
        {
            required: true,
            message: '请输入年龄',
            trigger: 'blur'
        }
    ],
    phone:[
        {
            required: true,
            message: '请输入手机号码',
            trigger: 'blur'
        }
    ],
    email:[
        {
            required: true,
            message: '请输入邮箱',
            trigger: 'blur'
        }
    ],
    userType:[
        {
            required: true,
            message: '请选择用户类型',
            trigger: 'blur'
        }
    ],
    roleId:[
        {
            required: true,
            message: '请选择用户角色',
            trigger: 'blur'
        }
    ],
    departId:[
        {
            required: true,
            message: '请选择部门',
            trigger: 'blur'
        }
    ],
    sectionId:[
        {
            required: true,
            message: '请选择班级',
            trigger: 'blur'
        }
    ]
})

//表格数据
const tableList = ref([])

//表格高度
const tableHeight = ref(0)

//查询列表
async function getList (){
    let res = await getUserList(searchParm)
    if(res && res.code == 0){
        tableList.value = res.data.records
        // 分页信息
        searchParm.total = res.data.total;
    }
}

//查询用户
function searchBtn(){
    getList()
}

//重置搜索框
function resetBtn(){
    searchParm.userNumber = '';
    searchParm.name = '';
    searchParm.currentPage = 1;
    getList()
}

//新增用户对话框
const openAdd = ref(false)
//编辑用户对话框
const openEdit = ref(false)

// 取消按钮
function cancel() {
  openEdit.value = false;
  openAdd.value = false;
  addRef.value?.resetFields()
}
//新增用户
function addBtn(){
    addRef.value?.resetFields()
    openAdd.value = true
}


//根据用户id查询角色
async function getRoleList(id:string){
    let res = await getRoleById(id)
    if(res && res.code == 0){
        addModel.roleId = res.data;
    }
}

async function getDepartList(id:string){
    let res = await getDepartById(id)
    if(res && res.code == 0){
        addModel.departId = res.data;
    }
}

async function getSectionList(id:string){
    let res = await getSectionById(id)
    if(res && res.code == 0){
        addModel.sectionId = res.data;
    }
}

//编辑用户
function editBtn(row:User){
    getRoleList(row.id)
    getDepartList(row.id)
    getSectionList(row.id)
    addRef.value?.resetFields()
    openEdit.value = true;
    nextTick(()=>{
        Object.assign(addModel,row)
    })
}

//删除用户
async function deleteBtn(id:string){
   const confirm = await global.$myconfirm("是否确认删除该用户")
   if(confirm){
       const res = await deleteUser(id)
       if(res && res.code === 0){
           ElMessage.success(res.msg)
           getList()
       }
   }
}

//页容量改变时触发
function handleSizeChange(size:number){
    searchParm.pageSize = size
    getList()
}

//页数改变时触发
function handleCurrentChange(currentPage:number){
    searchParm.currentPage = currentPage
    getList()
}



//新增提交按钮
function submitFormAdd() {
    addRef.value?.validate(async(valid) => {
        if (valid) {
            //提交请求
            let res = await addUser(addModel)
            if(res && res.code == 0){
                ElMessage.success(res.msg)
                getList()
                openAdd.value = false;
            }
        }
    });
    
}

//编辑提交按钮
function submitFormEdit() {
    addRef.value?.validate(async(valid) => {
        if (valid) {
            //提交请求
            let res = await editUser(addModel)
            if(res && res.code == 0){
                ElMessage.success(res.msg)
                getList()
                openEdit.value = false;
            }
        }
    });
}

async function resetPasswordBtn(id:string){
   const confirm = await global.$myconfirm("是否确认重置密码，重置后的密码为123456")
   if(confirm){
       const res = await resetPassword(id)
       if(res && res.code === 0){
           ElMessage.success(res.msg)
           getList()
       }
   }
}

const sexOptions = reactive([
    {value:'男',label:'男'},
    {value:'女',label:'女'}
])

const userTypeOptions = reactive([
    {value:'学生',label:'学生'},
    {value:'教师',label:'教师'},
    {value:'职工',label:'职工'},
])

type Option = {
    value:number,
    label:string
}

let Roleoptions = ref<Option[]>([])
let Departmentoptions = ref<Option[]>([])
let Sectionoptions = ref<Option[]>([])
const showsectionOption = ref(true);



watch(
  () => addModel.userType,
  (newValue) => {
    if (newValue === '职工' ||  newValue === '教师') {
      showsectionOption.value = false;
    } else {
      showsectionOption.value = true;
    }
  }
);

async function getSelectRole(){
    let res = await getRoleSelect()
    if(res && res.code == 0){
        Roleoptions.value = res.data;
    }
}


async function getSelectDepartment(){
    let res = await getDepartmentSelect()
    if(res && res.code == 0){
        Departmentoptions.value = res.data;
    }
}

async function getSelectSection(){
    let res = await getSectionSelect()
    if(res && res.code == 0){
        Sectionoptions.value = res.data;
    }
}


nextTick(() => {
    tableHeight.value = window.innerHeight - 400
    getList()
})
getList()
getSelectRole()
getSelectDepartment()
getSelectSection()
</script>

<style scoped>

</style>