// 报表 API
import request from './index'
import type { BusinessReportData, MemberReportData, SetmealReportData, Result } from '@/types/api'

export function getBusinessReportData(): Promise<Result<BusinessReportData>> {
  return request.get('/report/getBusinessReportData').then(r => r.data)
}

export function getMemberReport(): Promise<Result<MemberReportData>> {
  return request.get('/report/getMemberReport').then(r => r.data)
}

export function getSetmealReport(): Promise<Result<SetmealReportData[]>> {
  return request.get('/report/getSetmealReport').then(r => r.data)
}

export function exportBusinessReport(): Promise<Blob> {
  return request.get('/report/exportBusinessReport', { responseType: 'blob' }).then(r => r.data)
}
