import { atom, selectorFamily } from 'recoil';
import jwtApi from 'util/JwtApi';
import api from 'util/APIUtils';
import { qs, stringify } from 'qs';
import { userSimpleState } from 'recoil/user/userStore';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist();

export const announcementProcessState = atom({
  key: 'announcementProcessState',
  default: {},
  effects_UNSTABLE: [persistAtom],
});

export const announcementListDetailProcessSelector = selectorFamily({
  key: 'announcementListDetailProcessSelector',
  get:
    (announcementDetailId) =>
    async ({ get }) => {
      const userSimple = get(userSimpleState);
      let data = null;
      if (userSimple) {
        data = await jwtApi
          .get(`/api/announcements/${announcementDetailId}/process`)
          .then((response) => {
            console.log('logedin');
            console.log('Get/api/announcment/process');
            console.log(response);
            return response.data.result;
          });
      } else {
        console.log('[NOT LOGIN] API');
        data = await api
          .get(`/api/announcements/${announcementDetailId}/process`)
          .then((response) => {
            console.log('로그인 안 된 상태');
            console.log('Get/api/announcment/process');
            console.log(response);
            return response.data.result;
          });
      }
      return data;
    },
});
