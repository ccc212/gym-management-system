import http from "../../../http/index";
import type { Department, DepartmentListParm } from "./DepartmentModel";

//新增部门
export function addDepartment(parm:Department){
    return http.post("/api/system/department",parm)
}

//获取部门列表
export function getDepartmentList(parm:DepartmentListParm){
    return http.get("/api/system/department/getList",parm)
}

//删除部门
export function deleteDepartment(id:string){
    return http.delete(`/api/system/department/${id}`)
}

//编辑部门
export function editDepartment(parm:Department){
    return http.put("/api/system/department",parm)
}

//下拉部门
export function getDepartmentSelect(){
    return http.get("/api/system/department/selectList")
}