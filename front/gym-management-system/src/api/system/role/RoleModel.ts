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

//分配菜单数据类型
export type SaveMenuParm ={
    roleId:string;
    list:Array<string>;
}