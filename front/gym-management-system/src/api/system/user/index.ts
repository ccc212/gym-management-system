import http from "../../../http/index";
import type { User, UserListParm } from "./UserModel";

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
