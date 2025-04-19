<template>
    <el-main>
       <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>

       <el-table :data="tableList" row-key="id" style="margin-top: 20px;" default-expand-all border stripe>
        <el-table-column label="菜单名称" prop="title"></el-table-column>
        <el-table-column label="菜单图标" prop="icon">
            <template #default="scope">
                <el-icon v-if="scope.row.icon">
                <component  :is="scope.row.icon"></component>
            </el-icon>
            </template>
        </el-table-column>
        <el-table-column label="菜单类型" prop="type">
            <template #default="scope">
                <el-tag v-if="scope.row.type == '0'" type="danger" size="default" >目录</el-tag>
                <el-tag v-if="scope.row.type == '1'" type="success" size="default" >菜单</el-tag>
                <el-tag v-if="scope.row.type == '2'" type="primary" size="default" >按钮</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="上级菜单" prop="parentName"></el-table-column>
        <el-table-column label="路由名称" prop="name"></el-table-column>
        <el-table-column label="路由地址" prop="path"></el-table-column>
        <el-table-column label="组件路径" prop="url"></el-table-column>
        <el-table-column label="序号" prop="orderNum"></el-table-column>
        <el-table-column label="操作" width="220" align="center">
            <template #default="scope"  >
                <el-button type="primary" icon="Edit" size="default" @click="editBtn(scope.row)">编辑</el-button>
                <el-button type="danger" icon="Delete" size="default" @click="deleteBtn(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
       </el-table>


       <!-- 新增菜单对话框 -->
      <el-dialog title="新增菜单" v-model="openAdd" width="500px" append-to-body>
      <el-form ref="addRef" :model="addModel" :rules="rules" label-width="80px">
        <el-form-item label="菜单类型" prop="type">
            <el-radio-group v-model="addModel.type" >
                <el-radio label="0">目录</el-radio>
                <el-radio label="1">菜单</el-radio>
                <el-radio label="2">按钮</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="上级菜单" prop="parentId">
            <el-tree-select
                v-model="addModel.parentId"
                :data="treeList"
                @check-change="treeclick"
                :render-after-expand ="false"
                show-checkbox
                check-strictly
            ></el-tree-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="title">
            <el-input v-model="addModel.title" placeholder="请输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标" v-if="addModel.type != '2'" prop="icon">
            <el-input v-model="addModel.icon" placeholder="请输入图标"></el-input>
        </el-form-item>
        <el-form-item label="路由名称" v-if="addModel.type != '2'" prop="name">
            <el-input v-model="addModel.name" placeholder="请输入路由名称"></el-input>
        </el-form-item>
        <el-form-item label="菜单序号"  prop="orderNum">
            <el-input v-model="addModel.orderNum" placeholder="请输入菜单序号"></el-input>
        </el-form-item>
        <el-form-item label="权限字段" prop="code">
            <el-input v-model="addModel.code" placeholder="请输入路由名称"></el-input>
        </el-form-item>
        <el-form-item label="路由地址" v-if="addModel.type != '2'" prop="path">
            <el-input v-model="addModel.path" placeholder="请输入路由地址"></el-input>
        </el-form-item>
        <el-form-item label="组件路径" v-if="addModel.type == '1'" prop="url"> 
            <el-input v-model="addModel.url" placeholder="请输入组件路径"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFormAdd">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑菜单对话框 -->
    <el-dialog title="编辑菜单" v-model="openEdit" width="500px" append-to-body>
      <el-form ref="addRef" :model="addModel" :rules="rules" label-width="80px">
        <el-form-item label="菜单类型" prop="type">
            <el-radio-group v-model="addModel.type" >
                <el-radio label="0">目录</el-radio>
                <el-radio label="1">菜单</el-radio>
                <el-radio label="2">按钮</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="上级菜单" prop="parentId">
            <el-tree-select
                v-model="addModel.parentId"
                :data="treeList"
                @check-change="treeclick"
                :render-after-expand ="false"
                show-checkbox
                check-strictly
            ></el-tree-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="title">
            <el-input v-model="addModel.title" placeholder="请输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标" v-if="addModel.type != '2'" prop="icon">
            <el-input v-model="addModel.icon" placeholder="请输入图标"></el-input>
        </el-form-item>
        <el-form-item label="路由名称" v-if="addModel.type != '2'" prop="name">
            <el-input v-model="addModel.name" placeholder="请输入路由名称"></el-input>
        </el-form-item>
        <el-form-item label="菜单序号"  prop="orderNum">
            <el-input v-model="addModel.orderNum" placeholder="请输入菜单序号"></el-input>
        </el-form-item>
        <el-form-item label="权限字段" prop="code">
            <el-input v-model="addModel.code" placeholder="请输入路由名称"></el-input>
        </el-form-item>
        <el-form-item label="路由地址" v-if="addModel.type != '2'" prop="path">
            <el-input v-model="addModel.path" placeholder="请输入路由地址"></el-input>
        </el-form-item>
        <el-form-item label="组件路径" v-if="addModel.type == '1'" prop="url"> 
            <el-input v-model="addModel.url" placeholder="请输入组件路径"></el-input>
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
import {ref,reactive} from 'vue'
import type {FormInstance} from 'element-plus'
import {getParentApi,addApi,getListApi,editApi,deleteApi} from '../../../api/system/menu/index'
import {ElMessage} from 'element-plus'
import type {MenuType} from '../../../api/system/menu/MenuModel'
import { nextTick } from 'vue'
import useInstance from '../../../hooks/useInstance'

//获取全局
const {global} = useInstance()

//表单ref属性
const addRef = ref<FormInstance>()

//新增菜单对话框
const openAdd = ref(false)

//编辑菜单对话框
const openEdit = ref(false)

//获取上级菜单
const treeList = ref([])
async function getParent(){
    let res = await getParentApi()
    if(res && res.code == 0){
        treeList.value = res.data
    }
}

//表单绑定对象
const addModel = reactive({
    id:'',
    parentId:'',
    title:'',
    code:'',
    name:'',
    path:'',
    url:'',
    type:'',
    icon:'',
    parentName:'',
    orderNum:'',
})

//表单验证规则
const rules = reactive({
    parentId:[{
        required: true, 
        message: '请选择上级菜单', 
        trigger: 'blur' 
    }],
    title:[{
        required: true, 
        message: '请输入菜单名称', 
        trigger: 'blur' 
    }],
    orderNum:[{
        required: true, 
        message: '请输入菜单序号', 
        trigger: 'blur' 
    }],
    type:[{
        required: true, 
        message: '请选择菜单类型', 
        trigger: 'blur' 
    }],
    code:[{
        required: true, 
        message: '请输入权限字段', 
        trigger: 'blur' 
    }],
    name:[{
        required: true, 
        message: '请输入路由名称', 
        trigger: 'blur' 
    }],
    path:[{
        required: true, 
        message: '请输入路由地址', 
        trigger: 'blur' 
    }],
    url:[{
        required: true, 
        message: '请输入组件路径', 
        trigger: 'blur' 
    }],
    icon:[{
        required: true, 
        message: '请输入图标', 
        trigger: 'blur' 
    }],
})

//上级菜单选中事件
const treeclick = (item:any) => {
    addModel.parentName = item.title
}


// 取消按钮
function cancel() {
 /*  openEdit.value = false; */
  openAdd.value = false;
  openEdit.value = false;
  addRef.value?.resetFields()
}

//新增菜单
function addBtn(){
    addRef.value?.resetFields()
    openAdd.value = true
    getParent()
}

//编辑菜单按钮
async function editBtn(row:MenuType){
    await getParent()
    addRef.value?.resetFields()
    openEdit.value = true
    nextTick(()=>{
      Object.assign(addModel,row)
})
    
}
//删除菜单按钮
async function deleteBtn(id:string){
    const confirm = await global.$myconfirm("是否删除该菜单")
    if(confirm){
        const res = await deleteApi(id)
        if(res && res.code === 0){
            ElMessage.success(res.msg)
            getList()
        }
        ElMessage.error(res.msg)
    }
}

//新增提交按钮
function submitFormAdd() {
    addRef.value?.validate(async(valid) => {
        if (valid) {
            let res = await addApi(addModel)
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
            let res = await editApi(addModel)
            if(res && res.code == 0){
                ElMessage.success(res.msg)
                getList()
                openEdit.value = false;
            }
        }
    });
}

//获取表格数据
const tableList = ref([])
async function getList(){
    let res = await getListApi()
    if(res && res.code == 0){
        tableList.value = res.data
    }
}


getList()
</script>

<style scoped>

</style>