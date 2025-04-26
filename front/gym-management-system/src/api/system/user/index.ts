import http from "../../../http/index";
import type { User, UserListParm,Login,AssignParm,UpdateParm } from "./UserModel";

//新增用户
export function addUser(parm:User){
    return http.post("/api/system/user",parm)
}

//获取用户列表
export function getUserList(parm:UserListParm){
    return http.get("/api/system/user/getList",parm)
}

//删除用户
export function deleteUser(id:string){
    return http.delete(`/api/system/user/${id}`)
}

//编辑用户
export function editUser(parm:User){
    return http.put("/api/system/user",parm)
}

//查询角色id回显用
export function getRoleById(id:string){
    return http.get(`/api/system/user/getRoleId/`,{id:id})
}
//查询部门id回显用
export function getDepartById(id:string){
    return http.get(`/api/system/user/getDepartId/`,{id:id})
}
//查询班级id回显用
export function getSectionById(id:string){
    return http.get(`/api/system/user/getSectionId/`,{id:id})
}
//重置密码
export function resetPassword(id:string){
    return http.put(`/api/system/user/resetPassword/${id}`)
}

//登录
export function loginApi(parm:Login){
    return http.post("/api/system/user/login",parm)
}

//查询菜单树
export function getAssignTreeApi(parm:AssignParm){
    return http.get("/api/system/user/getAssignTree",parm)
}

//修改密码参数
export function updatePasswordApi(parm:UpdateParm){
    return http.post("/api/system/user/updatePassword",parm)
}

//获取用户信息
export function getInfoApi(id:string){
    return http.get("/api/system/user/getInfo",{id:id})
}