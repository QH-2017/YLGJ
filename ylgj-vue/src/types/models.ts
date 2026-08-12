// 医疗管家 - 数据模型类型定义
// 与后端 POJO 实体字段一一对应

export interface Setmeal {
  id?: number
  name: string
  code: string
  helpCode: string
  sex: string
  age: string
  price: number
  remark: string
  attention: string
  img: string
  checkgroupIds: number[]
  setmealCheckGroups?: SetmealCheckgroup[]
}

export interface SetmealCheckgroup {
  setmealId: number
  checkgroupId: number
}

export interface Checkgroup {
  id?: number
  code: string
  name: string
  helpCode: string
  sex: string
  remark: string
  attention: string
  checkitemIds: number[]
}

export interface Checkitem {
  id?: number
  code: string
  name: string
  sex: string
  age: string
  price: number
  type: string
  attention: string
  remark: string
}

export interface Member {
  id?: number
  fileNumber?: string
  name: string
  sex: string
  idCard: string
  phoneNumber: string
  regTime?: string
  password?: string
  email?: string
  birthday?: string
  remark?: string
}

export interface Order {
  id: number
  memberId: number
  orderDate: string
  orderType: string
  orderStatus: string
  setmealId: number
  member?: { id: number; name: string; phoneNumber: string; sex: string }
  setmeal?: { id: number; name: string; price: number }
}

export interface OrderEnriched {
  id: number
  orderDate: string
  orderType: string
  orderStatus: string
  setmealId: number
  member: { id: number; name: string; phoneNumber: string; sex: string }
  setmeal: { id: number; name: string; price: number }
}

export interface User {
  id?: number
  username: string
  password?: string
  gender?: string
  birthday?: string
  station?: string
  telephone?: string
  remark?: string
}

export interface Ordersetting {
  id?: number
  orderDate: string
  number: number
  reservations: number
}

// 预约提交参数
export interface OrderSubmitParams {
  name: string
  sex: string
  telephone: string
  idCard: string
  setmealId: string
  orderDate: string
}
