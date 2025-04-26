import http from "../../../http/index";
import type { Role, RoleListParm,SaveMenuParm } from "./RoleModel";

//新增角色
export function addRole(parm:Role){
    return http.post("/api/system/role",parm)
}

//获取角色列表
export function getRoleList(parm:RoleListParm){
    return http.get("/api/system/role/getList",parm)
}

//删除角色
export function deleteRole(id:string){
    return http.delete(`/api/system/role/${id}`)
}

//编辑角色
export function editRole(parm:Role){
    return http.put("/api/system/role",parm)
}

//下拉角色
export function getRoleSelect(){
    return http.get("/api/system/role/selectList")
}

//分配菜单保存
export function saveRoleMenuApi(parm:SaveMenuParm){
    return http.post("/api/system/role/saveRoleMenu",parm)
}