import http from "../../../http/index";
import type { Section, SectionListParm } from "./SectionModel";

//新增班级
export function addSection(parm:Section){
    return http.post("/api/system/section",parm)
}

//获取班级列表
export function getSectionList(parm:SectionListParm){
    return http.get("/api/system/section/getList",parm)
}

//删除班级
export function deleteSection(id:string){
    return http.delete(`/api/system/section/${id}`)
}

//编辑班级
export function editSection(parm:Section){
    return http.put("/api/system/section",parm)
}

//下拉班级
export function getSectionSelect(){
    return http.get("/api/system/section/selectList")
}