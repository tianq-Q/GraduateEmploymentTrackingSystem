import request from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  studentNumber: string
  password: string
  realName: string
  phone?: string
  email?: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  realName: string
  role: string
  avatar: string
}

export function login(data: LoginParams) {
  return request.post<LoginResult>('/auth/login', data)
}

export function register(data: RegisterParams) {
  return request.post<LoginResult>('/auth/register', data)
}
