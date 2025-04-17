//角色数据类型
export type Role = {
    id:string;
    roleName:string;
    remark:string;
}

//列表数据类型
export type RoleListParm = {
    currentPage:number;
    pageSize:number;
    roleName:string;
    total:number;
}