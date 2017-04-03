export function initSocket() {
	const socket = new SockJS('http://localhost:8080/gs-guide-websocket')
	// const socket = new SockJS('https://jwl-api-v0.herokuapp.com/gs-guide-websocket')
	return Stomp.over(socket)
}
