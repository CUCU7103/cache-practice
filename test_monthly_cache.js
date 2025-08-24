import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'], // 캐시 기대치: 더 빠른 응답 (예: 200ms)
        http_req_failed: ['rate<0.01'],
    },
};

const BASE_URL = 'http://localhost:8080/api/v1/movies';

export default function () {
    const month = 8;
    const res = http.get(`${BASE_URL}/month/cache/${month}`);

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

    sleep(1);
}
