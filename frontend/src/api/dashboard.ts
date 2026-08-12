import request from './request'

export interface DashboardStats {
  totalGraduates: number
  employedCount: number
  unemployedCount: number
  employmentRate: number
  pendingReviews: number
  totalDepartments: number
}

export interface EmploymentTrend {
  month: string
  rate: number
  count: number
}

export interface DepartmentStat {
  name: string
  total: number
  employed: number
  rate: number
}

export interface StatusDistribution {
  name: string
  value: number
}

export const getDashboardStats = () => request.get<DashboardStats>('/dashboard/stats')
export const getEmploymentTrend = () => request.get<EmploymentTrend[]>('/dashboard/trend')
export const getDepartmentStats = () => request.get<DepartmentStat[]>('/dashboard/department')
export const getStatusDistribution = () => request.get<StatusDistribution[]>('/dashboard/distribution')
