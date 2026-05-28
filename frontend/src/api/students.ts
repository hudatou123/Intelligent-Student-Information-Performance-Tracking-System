import api from '@/lib/axios'
import type { Student, PageResponse, ApiResponse } from '@/types'

export interface StudentCreatePayload {
  studentNumber: string
  firstName: string
  lastName: string
  email: string
  phone: string
  address: string
}

export interface StudentUpdatePayload {
  firstName?: string
  lastName?: string
  email?: string
  phone?: string
  address?: string
}

export const studentApi = {
  getAll: async (page = 0, size = 10, search = ''): Promise<PageResponse<Student>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Student>>>('/students', {
      params: { page, size, search: search || undefined },
    })
    return data.data
  },

  getById: async (id: number): Promise<Student> => {
    const { data } = await api.get<ApiResponse<Student>>(`/students/${id}`)
    return data.data
  },

  create: async (payload: StudentCreatePayload): Promise<Student> => {
    const { data } = await api.post<ApiResponse<Student>>('/students', payload)
    return data.data
  },

  update: async (id: number, payload: StudentUpdatePayload): Promise<Student> => {
    const { data } = await api.put<ApiResponse<Student>>(`/students/${id}`, payload)
    return data.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/students/${id}`)
  },
}
