import {useState, useEffect} from 'react';
import {ref, uploadBytes, getDownloadURL, listAll} from 'firebase/storage';
import {useRecoilValue, useRecoilState} from 'recoil';
import axios from 'axios';

import {storage} from '../../../imageup/firebase';
import {v4} from 'uuid';

// import recoil
import {actorProfile, actorPhotoUrl, actorPhotoLists} from '../../../recoil/actor/actorStore';

// import css
import '../../../components/Casting/Casting.css';
import './ActorPhoto.css';

function ActorPhotoUpload(props) {
    console.log('test');
    const [ActorPhotoUploaded, setActorPhotoUploaded] = useState(null);
    // const [actPhotoUrl, setActPhotoUrl] = useState('');
    // const [actorPhotos, setActorPhotos] = useRecoilState(actorPhotoLists);
    const ActorPhotoDirectory = useRecoilValue(actorPhotoUrl);
    const actorInfo = useRecoilValue(actorProfile);

    const ActorPhotosListRef = ref(storage, ActorPhotoDirectory);

    const token =
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDAwMDEiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZW1haWwiOiJkZGY5OThAZ21haWwuY29tIiwiZXhwIjoxNjc4MjU2MjEyfQ.gSBnEPdb7LPDgTMwi5fDDlEdYxgbdJ6hInbddudS9suerZhCPuHDV3P9C6ygWTacOvhfT9tS8i94LP1qSszc0w';

    const uploadFile = () => {
        if (ActorPhotoUploaded === null) return;
        const imageRef = ref(storage, `${ActorPhotoDirectory}/${ActorPhotoUploaded.name + v4()}`);
        uploadBytes(imageRef, ActorPhotoUploaded).then((snapshot) => {
            getDownloadURL(snapshot.ref).then((url) => {
                // setActPhotoUrl(url);
                const content = {
                    originName: `${actorInfo.name}'s picture`,
                    savedName: `picture ${actorInfo.name}`,
                    type: 'PICTURE',
                    url: url,
                };
                // axios.post(`http://i8a702.p.ssafy.io:8085/api/actors/${actorInfo.actorProfileId}/media`,
                axios
                    .post(`http://i8a702.p.ssafy.io:8085/api/actors/100001/media`, content, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    })
                    .then((res) => {
                        console.log('post success');
                    })
                    .catch((err) => {
                        console.log(err);
                    });
            });
        });
    };

    return (
        <div>
            {/*파이어베이스와의 연동을 통해 사진을 올리는 Component*/}
            <div className="photo-list">
                <div className="file-box">
                    <label for="file">
                        <img
                            src="/img/plus.png"
                            width={'200px'}
                            height={'200px'}
                            object-fit={'cover'}
                            className="object-center"
                        />
                    </label>
                    <input
                        type="file"
                        id="file"
                        onChange={(e) => {
                            setActorPhotoUploaded(e.target.files[0]);
                        }}
                    />

                    <button onClick={uploadFile}>사진 올리기</button>
                </div>
            </div>
        </div>
    );
}

export default ActorPhotoUpload;
