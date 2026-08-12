// API 通用响应类型

export interface Result<T = any> {
  flag: boolean
  message: string
  data: T
}

export interface PageResult<T = any> {
  total: number
  rows: T[]
}

export interface QueryPageBean {
  currentPage: number
  pageSize: number
  queryString: string
}

// 报表数据
export interface BusinessReportData {
  reportDate: string
  todayNewMember: number
  totalMember: number
  thisWeekNewMember: number
  thisMonthNewMember: number
  todayOrderNumber: number
  todayVisitsNumber: number
  thisWeekOrderNumber: number
  thisWeekVisitsNumber: number
  thisMonthOrderNumber: number
  thisMonthVisitsNumber: number
  hotSetmeal: Array<{ name: string; setmeal_count: number; proportion: string }>
}

export interface MemberReportData {
  months: string[]
  memberCounts: number[]
}

export interface SetmealReportData {
  name: string
  value: number
}
