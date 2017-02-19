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
