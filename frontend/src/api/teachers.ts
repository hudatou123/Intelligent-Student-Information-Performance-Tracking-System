import api from '@/lib/axios'
import type { Teacher, PageResponse, ApiResponse } from '@/types'

export interface TeacherCreatePayload {
  employeeId: string
  fullName: string
  department: string
  phone: string
}

export interface TeacherUpdatePayload {
  fullName?: string
  department?: string
  phone?: string
}

export const teacherApi = {
  getAll: async (page = 0, size = 10, search = ''): Promise<PageResponse<Teacher>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Teacher>>>('/teachers', {
      params: { page, size, search: search || undefined },
    })
    return data.data
  },

  getById: async (id: number): Promise<Teacher> => {
    const { data } = await api.get<ApiResponse<Teacher>>(`/teachers/${id}`)
    return data.data
  },

  create: async (payload: TeacherCreatePayload): Promise<Teacher> => {
    const { data } = await api.post<ApiResponse<Teacher>>('/teachers', payload)
    return data.data
  },

  update: async (id: number, payload: TeacherUpdatePayload): Promise<Teacher> => {
    const { data } = await api.put<ApiResponse<Teacher>>(`/teachers/${id}`, payload)
    return data.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/teachers/${id}`)
  },
}
