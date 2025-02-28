document.addEventListener("DOMContentLoaded", async function () {
    console.log("DOM fully loaded and parsed.");

    // 🏷️ Select elements safely
    const sign_in_btn = document.querySelector("#sign-in-btn");
    const sign_up_btn = document.querySelector("#sign-up-btn");
    const container = document.querySelector(".container");

    if (sign_up_btn && container) {
        sign_up_btn.addEventListener("click", () => container.classList.add("sign-up-mode"));
    }
    if (sign_in_btn && container) {
        sign_in_btn.addEventListener("click", () => container.classList.remove("sign-up-mode"));
    }

    // 🚀 Sign-in Form Handling   
    $("#signin-form").submit(async function (event) {
        event.preventDefault();

        let userData = {
            email: $("#signin-email").val(),
            password: $("#signin-password").val()
        };

        try {
            let response = await fetch("http://localhost:8080/api/signin", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData),
                credentials: "include"
            });

            let result = await response.text();

            if (response.ok) {
                sessionStorage.setItem("isLoggedIn", "true"); // ✅ Store session state
                alert(result);
                setTimeout(() => window.location.href = "UserIndex.html", 100);
            } else {
                alert(result);
            }
        } catch (error) {
            console.error("Fetch Error:", error);
            alert("An error occurred. Please try again.");
        }
    });

    // 🚀 Sign-up Form Handling   
    $("#signup-form").submit(async function (event) {
        event.preventDefault();

        let userData = {
            username: $("#signup-username").val(),
            email: $("#signup-email").val(),
            password: $("#signup-password").val(),
        };

        try {
            let response = await fetch("http://localhost:8080/api/createUser", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData),
                credentials: "include"
            });

            let result = await response.text();

            if (response.ok) {
                alert(result);
                setTimeout(() => container.classList.remove("sign-up-mode"), 100);
            } else {
                alert(result);
            }
        } catch (error) {
            console.error("Fetch Error:", error);
            alert("An error occurred. Please try again.");
        }
    });

    // 🏷️ Search Button
    const searchBtn = document.getElementById("search-btn");
    const searchInput = document.getElementById("searchInput");

    if (searchBtn && searchInput) {
        searchBtn.addEventListener("click", function () {
            let query = searchInput.value.trim().toLowerCase();
            if (!query) {
                alert("⚠️ Please enter a search term.");
                return;
            }

            let data = ["Java", "SpringBoot", "Python"];
            let lowerCaseData = data.map(item => item.toLowerCase());

            let index = lowerCaseData.indexOf(query);
            if (index !== -1) {
                let selectedLanguage = data[index]; 
                window.location.href = `notes.html?language=${selectedLanguage}`;
                return;
            } else {
                let suggestions = lowerCaseData.filter(item => item.includes(query))
                    .map(item => data[lowerCaseData.indexOf(item)]);

                alert(suggestions.length > 0
                    ? `❌ "${query}" not found.\nDid you mean: ${suggestions.join(", ")}?`
                    : `❌ "${query}" not found.\nNo similar terms available.`);
            }
        });
    } else {
        console.error("❌ Search button or input field not found.");
    }

    // 🚀 Logout Button Handling
    const logoutBtn = document.getElementById("logout-btn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", async function () {
            try {
                let response = await fetch("http://localhost:8080/logout", {
                    method: "GET",
                    credentials: "include",
                });

                if (response.ok) {
                    alert("✅ Logout successful!");
                    sessionStorage.removeItem("isLoggedIn");
                    window.location.href = "http://localhost:5500/index.html";
                } else {
                    alert("❌ Logout failed! Please try again.");
                }
            } catch (error) {
                console.error("🚨 Logout Error:", error);
                alert("⚠️ An error occurred while logging out.");
            }
        });
    } else {
        console.warn("❌ Logout button not found.");
    }

    // 🚀 Fetch Notes
    async function fetchNotes() {
        const urlParams = new URLSearchParams(window.location.search);
        const language = urlParams.get("language");

        if (language) {
            const languageTitle = document.getElementById("languageTitle");
            const notesContainer = document.getElementById("notesContainer");

            if (languageTitle) languageTitle.innerText = `${language} Notes`;

            if (notesContainer) {
                try {
                    let response = await fetch(`http://localhost:8080/api/notes/${language}`, {
                        method: "GET",
                        credentials: "include",
                    });

                    if (response.status === 401) {
                        alert("You need to log in to view notes.");
                        window.location.href = "/index.html";
                        return;
                    }

                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }

                    let data = await response.json();
                    console.log("📜 Fetched Notes:", data);

                    if (data.length > 0) {
                        let fileId = data[0].fileId;
                        let apiKey = "AIzaSyD05OASXlmL34Ck4Bic767RUydNUIZBR9Q";
                        let pdfUrl = `https://www.googleapis.com/drive/v3/files/${fileId}?alt=media&key=${apiKey}`;

                        renderPDF(pdfUrl);
                    } else {
                        notesContainer.innerHTML = "<p>No notes available for this language. It will be available soon 😊</p>";
                    }

                } catch (error) {
                    console.error("❌ Fetch Error:", error);
                    notesContainer.innerHTML = `<p>Error fetching notes. Please try again.</p>`;
                }
            }
        }
    }



    async function renderPDF(pdfUrl) {
        const pdfjsLib = window["pdfjs-dist/build/pdf"];
        pdfjsLib.GlobalWorkerOptions.workerSrc = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.worker.min.js";

        let pdf;
        try {
            pdf = await pdfjsLib.getDocument(pdfUrl).promise;
        } catch (error) {
            console.error("❌ Error loading PDF:", error);
            document.getElementById("notesContainer").innerHTML = `<p style="Justify-content:center">⚠️ PDF could not be loaded. Please try again later.</p>`;
            return;
        }

        let pageNum = 1;
        let totalPages = pdf.numPages;

        const canvas = document.createElement("canvas");
        canvas.id = "pdfCanvas";
        canvas.style.maxWidth = "100%";
        canvas.style.display = "block";
        canvas.style.margin = "auto";

        const controls = document.createElement("div");
        controls.innerHTML = `
        <button id="prevPage">⬅ Prev</button>
        <span id="pageInfo">Page ${pageNum} of ${totalPages}</span>
        <button id="nextPage">Next ➡</button>
    `;
        controls.style.textAlign = "center";
        controls.style.margin = "10px 0";

        const notesContainer = document.getElementById("notesContainer");
        notesContainer.innerHTML = "";
        notesContainer.appendChild(canvas);
        notesContainer.appendChild(controls);

        const renderPage = async (num) => {
            let page = await pdf.getPage(num);
            let viewport = page.getViewport({ scale: 1.5 }); 

            let context = canvas.getContext("2d");
            canvas.width = viewport.width;
            canvas.height = viewport.height;

            let renderContext = {
                canvasContext: context,
                viewport: viewport,
            };

            await page.render(renderContext).promise;
            document.getElementById("pageInfo").innerText = `Page ${num} of ${totalPages}`;
        };

        // Initial render
        await renderPage(pageNum);

        document.getElementById("prevPage").addEventListener("click", () => {
            if (pageNum > 1) {
                pageNum--;
                renderPage(pageNum);
            }
        });

        document.getElementById("nextPage").addEventListener("click", () => {
            if (pageNum < totalPages) {
                pageNum++;
                renderPage(pageNum);
            }
        });
    }


    // Call fetchNotes when the page loads
    fetchNotes();
});
