import api from '@/lib/axios'
import type { AuthResponse, ApiResponse } from '@/types'

interface RegisterPayload {
  username: string
  password: string
  fullName: string
  email: string
  role: string
}

export const authApi = {
  login: async (username: string, password: string): Promise<AuthResponse> => {
    const { data } = await api.post<ApiResponse<AuthResponse>>('/auth/login', { username, password })
    return data.data
  },
  register: async (payload: RegisterPayload): Promise<AuthResponse> => {
    const { data } = await api.post<ApiResponse<AuthResponse>>('/auth/register', payload)
    return data.data
  },
  logout: async (): Promise<void> => {
    await api.post('/auth/logout')
  },
}
