export interface LoginResponse {
  token: string
  userId: number
  username: string
  realName: string
  role: string
  avatar?: string
  deptId?: number
}
