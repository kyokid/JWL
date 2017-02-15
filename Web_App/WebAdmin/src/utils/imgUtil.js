export function loadUserInputImg() {
	const fileInput = document.getElementById("inputImg")
	fileInput.onchange = function () {
		if (this.files && this.files[0]) {
			const reader = new FileReader()
			reader.readAsDataURL(this.files[0])
			reader.onload = imageIsLoaded
		}
	};

	function imageIsLoaded(e) {
		const uploadImg = document.getElementById("uploadImg")
		uploadImg.setAttribute("src", e.target.result)
		uploadImg.style.display = "block"
	}
}

export function submitImg() {
	const fileInput = document.getElementById("inputImg")
	const fd = new FormData()
	fd.append("img", fileInput.files[0])
	const xhttp = new XMLHttpRequest()
	xhttp.onreadystatechange = function () {
		if (this.readyState === 4 && this.status === 200) {
			const xmlString = this.responseXML
			const imgUrl = xmlString.getElementsByTagName("img_url")[0].innerHTML
			console.log(imgUrl)
			return imgUrl
			// document.getElementsByName("imgUrl")[0].value = imgUrl;
		} else {
			// TODO: error code here.
		}
	}
	xhttp.open("POST", "http://uploads.im/api?upload&resize_width=50&format=xml", true)
	xhttp.send(fd)
}
