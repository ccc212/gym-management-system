//班级数据类型
export type Section = {
    id:string;
    sectionName:string;
    remark:string;
}

//列表数据类型
export type SectionListParm = {
    currentPage:number;
    pageSize:number;
    sectionName:string;
    total:number;
}