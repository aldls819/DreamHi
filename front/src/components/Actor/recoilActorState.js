import { atom } from 'recoil'

export const ActorId = atom({
  key: 'ActorId',
  default: 'locker'
})

export const ActorFilmoUrl = atom({
  key: 'ActorFilmoUrl',
  default: null
})
export const ActorPhotoUrl = atom({
  key: 'ActorPhotoUrl',
  default: `images/${ActorId}/photo`
})

export const ActorVideoUrl = atom({
  key: 'ActorVideoUrl',
  default: `images/${ActorId}/video`
})