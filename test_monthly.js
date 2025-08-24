import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

const BASE_URL = 'http://localhost:8080/api/v1/movies';

export default function () {
    const month = 8; // 테스트할 달(month)을 여기에 설정
    const res = http.get(`${BASE_URL}/month/${month}`);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'body is JSON': (r) => {
            try {
                JSON.parse(r.body);
                return true;
            } catch (e) {
                return false;
            }
        },
    });

    sleep(1); // 각 VU 당 1초 휴식
}
