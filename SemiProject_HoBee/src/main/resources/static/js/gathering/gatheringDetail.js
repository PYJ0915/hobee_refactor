window.addEventListener("load", () => {
    const mapContainer = document.querySelector("#gatheringMap");
    if (!mapContainer || !gatheringLat || !gatheringLng) return;

    const mapOption = {
        center: new kakao.maps.LatLng(gatheringLat, gatheringLng),
        level: 3
    };
    const map = new kakao.maps.Map(mapContainer, mapOption);

    const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(gatheringLat, gatheringLng)
    });
    marker.setMap(map);

    const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:6px 10px; font-size:13px;">${gatheringPlace}</div>`
    });
    infowindow.open(map, marker);

    // 참여/취소/확정 버튼은 DOMContentLoaded에서 별도로 처리
});

document.addEventListener("DOMContentLoaded", () => {

     // 지도 초기화 함수
    function initMap() {
        const mapContainer = document.querySelector("#gatheringMap");
        if (!mapContainer) return;
        if (!gatheringLat || !gatheringLng || gatheringLat === 0) return;

        const mapOption = {
            center: new kakao.maps.LatLng(gatheringLat, gatheringLng),
            level: 3
        };
        const map = new kakao.maps.Map(mapContainer, mapOption);

        const marker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(gatheringLat, gatheringLng)
        });
        marker.setMap(map);

        const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:6px 10px; font-size:13px;">${gatheringPlace}</div>`
        });
        infowindow.open(map, marker);
    }

    // autoload=false일 때 명시적 로드
    if (typeof kakao !== "undefined") {
        kakao.maps.load(() => {
            initMap();
        });
    }

    // ========================
    //  참여 신청
    // ========================
    const joinBtn = document.querySelector("#joinBtn");
    const cancelBtn = document.querySelector("#cancelBtn");
    const confirmBtn = document.querySelector("#confirmBtn");

    if (joinBtn) {
        joinBtn.addEventListener("click", async () => {
            const gatheringNo = joinBtn.dataset.gatheringNo;
            const result = await fetch(`/gathering/join/${gatheringNo}`, {
                method: "POST"
            }).then(r => r.json());

            if (result.success) {
                alert(result.message);
                location.reload();
            } else {
                alert(result.message);
            }
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener("click", async () => {
            if (!confirm("참여를 취소하시겠습니까?")) return;
            const gatheringNo = cancelBtn.dataset.gatheringNo;
            await fetch(`/gathering/cancel/${gatheringNo}`, { method: "POST" });
            location.reload();
        });
    }

    if (confirmBtn) {
        confirmBtn.addEventListener("click", async () => {
            if (!confirm("모임을 확정하시겠습니까? 참여자들에게 채팅방이 생성됩니다.")) return;
            const gatheringNo = confirmBtn.dataset.gatheringNo;
            const result = await fetch(`/gathering/confirm/${gatheringNo}`, {
                method: "POST"
            }).then(r => r.json());

            if (result.success) {
                alert("모임이 확정되었습니다! 채팅방으로 이동합니다.");
                location.href = `/chat`;
            }
        });
    }
});