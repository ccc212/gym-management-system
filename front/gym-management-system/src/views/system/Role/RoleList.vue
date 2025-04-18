<template>
    <el-main>
       <el-form :model="searchParm" :inline="true" size="default">
        <el-form-item>
            <el-input v-model="searchParm.roleName" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button icon="Search" @click="searchBtn">搜索</el-button>
            <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
            <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
        </el-form-item>
       </el-form>

       <!-- 表格 -->
       <el-table :height="tableHeight":data="tableList" border stripe>
        <el-table-column prop="id" label="序号"></el-table-column>
        <el-table-column prop="roleName" label="角色名称"></el-table-column>
        <el-table-column prop="remark" label="角色备注"></el-table-column>
        <el-table-column label="操作" width="200px">
            <template #default="scope">
                <el-button type="primary" icon="Edit" size="small" @click="editBtn(scope.row)">编辑</el-button>
                <el-button type="danger" icon="Delete" size="small" @click="deleteBtn(scope.row.id)">删除</el-button>
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
      <el-dialog title="新增角色" v-model="openAdd" width="500px" append-to-body>
      <el-form ref="addRef" :model="addModel" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="addModel.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色备注" prop="remark">
          <el-input v-model="addModel.remark" placeholder="请输入角色备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFormAdd">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑角色对话框 -->
    <el-dialog title="编辑角色" v-model="openEdit" width="500px" append-to-body>
      <el-form ref="addRef" :model="addModel" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="addModel.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色备注" prop="remark">
          <el-input v-model="addModel.remark" placeholder="请输入角色备注" />
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
import {reactive,ref} from 'vue'
import type {FormInstance} from 'element-plus'
import {ElMessage} from 'element-plus'
import {addRole,getRoleList,deleteRole,editRole} from '../../../api/system/role/index'
import type {Role} from '../../../api/system/role/RoleModel'
import { nextTick } from 'vue'
import useInstance from '../../../hooks/useInstance'

//获取全局
const {global} = useInstance()

//表单ref属性
const addRef = ref<FormInstance>()

//表单的对象
const searchParm = reactive({
    id:'',
    currentPage:1,
    pageSize:10,
    roleName:'',
    total:0
})

//新增表单对象
const addModel = reactive({
    id:'',
    roleName:'',
    remark:''
})

//表单验证规则
const rules = reactive({
    roleName:[
        {
            required: true,
            message: '请输入角色名称',
            trigger: 'blur'
        }
    ],
    remark:[
        {
            required: true,
            message: '请输入角色备注',
            trigger: 'blur'
        }
    ],
})

//表格数据
const tableList = ref([])

//表格高度
const tableHeight = ref(0)

//查询列表
async function getList (){
    let res = await getRoleList(searchParm)
    if(res && res.code == 0){
        tableList.value = res.data.records
        // 分页信息
        searchParm.total = res.data.total;
    }
}

//查询角色
function searchBtn(){
    getList()
}

//重置搜索框
function resetBtn(){
    searchParm.roleName = ''
    searchParm.currentPage = 1;
    getList()
}

//新增角色对话框
const openAdd = ref(false)
//编辑角色对话框
const openEdit = ref(false)

// 取消按钮
function cancel() {
  openEdit.value = false;
  openAdd.value = false;
}
//新增角色
function addBtn(){
    openAdd.value = true
    addRef.value?.resetFields()
}

//编辑角色
function editBtn(row:Role){
    openEdit.value = true;
    addRef.value?.resetFields()
    nextTick(()=>{
        Object.assign(addModel,row)
    })
}

//删除角色
async function deleteBtn(id:string){
   const confirm = await global.$myconfirm("是否确认删除该角色")
   if(confirm){
       const res = await deleteRole(id)
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
            let res = await addRole(addModel)
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
            let res = await editRole(addModel)
            if(res && res.code == 0){
                ElMessage.success(res.msg)
                getList()
                openEdit.value = false;
            }
        }
    });
}

nextTick(() => {
    tableHeight.value = window.innerHeight - 400
    getList()
})
getList()

</script>

<style scoped>


</style>