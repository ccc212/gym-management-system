export interface Venue {
  id: string
  name: string
  type: string
  description: string
  status: 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE'
  price: number
  images: string[]
}

export interface Reservation {
  id: string
  venueInfo: Venue
  userId: string
  startTime: string
  endTime: string
  usageStatus: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
  cost: number
  actualStartTime?: string
  actualEndTime?: string
  duration?: string
  actualCost?: number
  paymentMethod?: 'balance' | 'wechat' | 'alipay'
  paymentTime?: string
} 