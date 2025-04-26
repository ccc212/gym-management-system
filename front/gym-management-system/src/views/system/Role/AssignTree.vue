<template>
    <el-dialog :title="titlename" v-model="open" width="500px"  append-to-body>
        <el-tree
             node-key="id"
             ref="treeRef" 
             default-expand-all 
             show-checkbox 
             :data="assignTreeData.list" 
             :props="defaultProps"
             :default-checked-keys="assignTreeData.assignTreeChecked"
             />

   <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import {getAssignTreeApi} from '../../../api/system/user/index'
import {saveRoleMenuApi} from '../../../api/system/role/index'
import {reactive,ref} from 'vue'
import {userStore} from '../../../store/user'
import {ElMessage,ElTree} from 'element-plus'

const store = userStore()

const titlename = ref()

const treeRef = ref<InstanceType<typeof ElTree>>()

//弹窗定义
const open = ref(false)

//数的属性配置
const defaultProps = {
    children: 'children',
    label: 'title'
}

//树数据
const assignTreeData = reactive({
    list:[],
    assignTreeChecked:[]
})

//数据查询的参数
const parms = reactive({
    roleId:'',
    userId:''
})

//查询菜单树
async function getAssignTree(){
    let res = await getAssignTreeApi(parms)
    if( res && res.code == 0){
        assignTreeData.list = res.data.menuList
        assignTreeData.assignTreeChecked = res.data.checkList
        //数据回显，判断角色原来是否已经分配过全新啊，如果有，回显
        if(assignTreeData.assignTreeChecked.length > 0){
            let newArr:any = [];
            assignTreeData.assignTreeChecked.forEach((item) =>{
                checked(item,assignTreeData.list,newArr);
            });
            assignTreeData.assignTreeChecked = newArr;
        }
    }
    
}

function cancel(){
    open.value = false
    assignTreeData.assignTreeChecked = []
    assignTreeData.list = []
}

//回显
function checked(id:number,data:any,newArr:any){
    data.forEach((item:any)=>{
        if(item.id == id){
            if(item.children && item.children.length == 0){
                newArr.push(item.id)
            }
        }else {
            if(item.children && item.children.length != 0)
                checked(id,item.children,newArr)
        }
    });
};

//提交的数据
const submitPram = reactive({
    roleId:'',
    list:[] as string []
})


//弹窗显示
async function show(id:string,roleName:string){
    submitPram.roleId = id;
    assignTreeData.assignTreeChecked = [];
    assignTreeData.list = [];
    submitPram.list = [];
    parms.roleId = id;
    parms.userId = store.getId;
    titlename.value = roleName;
    await getAssignTree();
    open.value = true;
}
//暴露
defineExpose({
   show
})


//提交表单
async function submitForm(){
    //获取选择的菜单的数据
    const checkIds = treeRef.value?.getCheckedKeys() as string[]
    const halfCheckIds = treeRef.value?.getHalfCheckedKeys() as string[]
    let ids = checkIds?.concat(halfCheckIds);
    //设置选中的节点
    submitPram.list = ids
    //判断是否已经选择菜单
    if(ids.length == 0){
        ElMessage.warning('请选择菜单')
        return
    }
    let res = await saveRoleMenuApi(submitPram)
    if(res.code == 0 && res){
        ElMessage.success(res.msg)
        open.value = false;
    }
}



</script>

<style scoped>

</style>