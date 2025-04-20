//用户数据类型
export type User = {
    id:string;
    userNumber:string;
    password:string;
    name:string;
    sex:string;
    age:string;
    phone:string;
    email:string;
    userType:string;
    roleId:string;
    departId:string;
    sectionId:string;
}

//列表数据类型
export type UserListParm = {
    currentPage:number;
    pageSize:number;
    total:number;
    userNumber:string;
    name:string;
    userType:string;
}

//登录
export type Login = {
    userNumber:string;
    password:string;
}

//菜单树参数
export type AssignParm = {
    roleId:string;
    userId:string;
}
