import { useEffect, useRef, useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { AlertCircle, Bot, RotateCcw, Send, Sparkles, User } from 'lucide-react'
import { chatApi } from '@/api/chat'
import { useAuthStore } from '@/store/authStore'
import { Button } from '@/components/ui/Button'
import { cn } from '@/lib/utils'

interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  error?: boolean
}

const SUGGESTIONS = [
  'What can you help students with?',
  'How do I check my grades?',
  'Explain how the grading system works.',
  'Give me tips to improve my study performance.',
]

function Avatar({ role }: { role: 'user' | 'assistant' }) {
  return role === 'assistant' ? (
    <div className="flex-shrink-0 w-8 h-8 rounded-lg bg-blue-100 flex items-center justify-center">
      <Bot className="h-4 w-4 text-blue-600" />
    </div>
  ) : (
    <div className="flex-shrink-0 w-8 h-8 rounded-lg bg-gray-200 flex items-center justify-center">
      <User className="h-4 w-4 text-gray-600" />
    </div>
  )
}

function MessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === 'user'
  return (
    <div className={cn('flex items-start gap-3', isUser && 'flex-row-reverse')}>
      <Avatar role={message.role} />
      <div
        className={cn(
          'max-w-[80%] px-4 py-2.5 text-sm whitespace-pre-wrap break-words rounded-2xl',
          isUser
            ? 'bg-blue-600 text-white rounded-tr-sm'
            : message.error
              ? 'bg-red-50 text-red-700 border border-red-200 rounded-tl-sm'
              : 'bg-gray-100 text-gray-800 rounded-tl-sm'
        )}
      >
        {message.error && <AlertCircle className="inline h-4 w-4 mr-1 -mt-0.5" />}
        {message.content}
      </div>
    </div>
  )
}

function TypingDots() {
  return (
    <div className="flex gap-1">
      <span className="w-2 h-2 rounded-full bg-gray-400 animate-bounce [animation-delay:-0.3s]" />
      <span className="w-2 h-2 rounded-full bg-gray-400 animate-bounce [animation-delay:-0.15s]" />
      <span className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" />
    </div>
  )
}

export default function AiAssistant() {
  const { user } = useAuthStore()
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [input, setInput] = useState('')
  const [conversationId, setConversationId] = useState<string | undefined>(undefined)
  const scrollRef = useRef<HTMLDivElement>(null)
  const textareaRef = useRef<HTMLTextAreaElement>(null)
  const idRef = useRef(0)
  const nextId = () => String(++idRef.current)

  const mutation = useMutation({
    mutationFn: (message: string) => chatApi.send(message, conversationId),
    onSuccess: (data) => {
      setConversationId(data.conversationId)
      setMessages((prev) => [
        ...prev,
        { id: nextId(), role: 'assistant', content: data.reply },
      ])
    },
    onError: () => {
      setMessages((prev) => [
        ...prev,
        {
          id: nextId(),
          role: 'assistant',
          content:
            'Sorry, something went wrong while contacting the assistant. Please try again.',
          error: true,
        },
      ])
    },
  })

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' })
  }, [messages, mutation.isPending])

  const send = () => {
    const text = input.trim()
    if (!text || mutation.isPending) return
    setMessages((prev) => [...prev, { id: nextId(), role: 'user', content: text }])
    setInput('')
    mutation.mutate(text)
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey && !e.nativeEvent.isComposing) {
      e.preventDefault()
      send()
    }
  }

  const resetConversation = () => {
    setMessages([])
    setConversationId(undefined)
    mutation.reset()
    textareaRef.current?.focus()
  }

  const isEmpty = messages.length === 0

  return (
    <div className="h-full flex flex-col">
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Bot className="h-6 w-6 text-blue-600" />
            AI Assistant
          </h1>
          <p className="text-gray-500 mt-1 text-sm">
            Ask about students, courses, and academic performance.
          </p>
        </div>
        {!isEmpty && (
          <Button variant="outline" size="sm" onClick={resetConversation}>
            <RotateCcw className="h-4 w-4" />
            New chat
          </Button>
        )}
      </div>

      {/* Chat panel */}
      <div className="flex-1 min-h-0 flex flex-col bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
        {/* Messages */}
        <div ref={scrollRef} className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-4">
          {isEmpty ? (
            <div className="h-full flex flex-col items-center justify-center text-center max-w-md mx-auto">
              <div className="w-14 h-14 rounded-2xl bg-blue-100 flex items-center justify-center mb-4">
                <Sparkles className="h-7 w-7 text-blue-600" />
              </div>
              <h2 className="text-lg font-semibold text-gray-900">
                Hi {user?.fullName?.split(' ')[0] ?? 'there'}, how can I help?
              </h2>
              <p className="text-sm text-gray-500 mt-1 mb-6">
                I'm your academic assistant. Try one of these:
              </p>
              <div className="grid gap-2 w-full">
                {SUGGESTIONS.map((s) => (
                  <button
                    key={s}
                    onClick={() => {
                      setInput(s)
                      textareaRef.current?.focus()
                    }}
                    className="text-left text-sm px-4 py-2.5 rounded-lg border border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300 transition-colors"
                  >
                    {s}
                  </button>
                ))}
              </div>
            </div>
          ) : (
            messages.map((m) => <MessageBubble key={m.id} message={m} />)
          )}

          {/* Typing indicator */}
          {mutation.isPending && (
            <div className="flex items-start gap-3">
              <Avatar role="assistant" />
              <div className="bg-gray-100 rounded-2xl rounded-tl-sm px-4 py-3">
                <TypingDots />
              </div>
            </div>
          )}
        </div>

        {/* Input bar */}
        <div className="border-t border-gray-200 p-3 sm:p-4 bg-white">
          <div className="flex items-end gap-2">
            <textarea
              ref={textareaRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              rows={1}
              placeholder="Type your message…  (Enter to send, Shift+Enter for new line)"
              className="flex-1 resize-none max-h-32 rounded-lg border border-gray-300 px-3 py-2 text-sm shadow-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <Button
              onClick={send}
              disabled={!input.trim() || mutation.isPending}
              loading={mutation.isPending}
              className="h-10"
            >
              <Send className="h-4 w-4" />
              <span className="hidden sm:inline">Send</span>
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
