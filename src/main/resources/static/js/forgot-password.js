document.getElementById("forgotPasswordForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // Ngăn trang load lại

    const email = document.getElementById("email").value;
    const messageElem = document.getElementById("message");

    try {
        const response = await fetch("http://localhost:8080/api/auth/forgot-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        });


        const data = await response.json();
        if (response.ok) {
            messageElem.innerHTML = "Hướng dẫn đặt lại mật khẩu đã được gửi!";
            messageElem.style.color = "green";
        } else {
            messageElem.innerHTML = data.message || "Lỗi khi gửi yêu cầu!";
            messageElem.style.color = "red";
        }
    } catch (error) {
        messageElem.innerHTML = "Có lỗi xảy ra, vui lòng thử lại!";
        messageElem.style.color = "red";
    }
});
