import api from '@/lib/axios'
import type { ApiResponse, ChatResponse } from '@/types'

export const chatApi = {
  send: async (message: string, conversationId?: string): Promise<ChatResponse> => {
    const { data } = await api.post<ApiResponse<ChatResponse>>('/chat', {
      message,
      conversationId: conversationId ?? null,
    })
    return data.data
  },
}
