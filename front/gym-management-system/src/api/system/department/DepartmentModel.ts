//部门数据类型
export type Department = {
    id:string;
    departName:string;
    departStuorfac:string;
}

//列表数据类型
export type DepartmentListParm = {
    currentPage:number;
    pageSize:number;
    departName:string;
    departStuorfac:string;
    total:number;
}