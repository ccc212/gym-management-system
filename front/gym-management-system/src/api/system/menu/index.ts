import http from "../../../http/index";
import type{ MenuType } from "./MenuModel";

//获取上级菜单
export function getParentApi() {
    return http.get("/api/system/menu/getParent");
}

//新增
export function addApi(parm:MenuType) {
    return http.post("/api/system/menu",parm);
}

//列表
export function getListApi() {
    return http.get("/api/system/menu/list");
}

//编辑
export function editApi(parm:MenuType) {
    return http.put("/api/system/menu",parm);
}

//删除
export function deleteApi(id:string){
    return http.delete(`/api/system/menu/${id}`)
}