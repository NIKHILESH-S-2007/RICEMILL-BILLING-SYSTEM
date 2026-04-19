<!DOCTYPE html>
<html lang="ta">
<head>
    <meta charset="UTF-8">
    <title>BILL ENTRY</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        /* 🔹 Login box style */
        #loginBox {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            background-color: #eaf2ff;
        }

        .login-container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            text-align: center;
            width: 300px;
        }

        .login-container h2 {
            color: #007bff;
            margin-bottom: 20px;
        }

        .login-container input {
            width: 90%;
            padding: 10px;
            margin: 8px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .login-container button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px;
            width: 100%;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
        }

        .login-container button:hover {
            background-color: #0056b3;
        }

        .error {
            color: red;
            font-size: 14px;
            margin-top: 10px;
        }

        /* 🔹 Bill form styles */
        #billPage {
            display: none;
            padding: 20px;
        }

        input { margin: 5px; }
        table, th, td { border: 1px solid black; border-collapse: collapse; padding: 5px; }
        .inline { display: inline-block; width: 45%; vertical-align: top; }
        .right { text-align: right; }
        .dot-line { border-bottom: 1px dotted #000; width: 250px; display: inline-block; }
        .totals { margin-top: 20px; font-size: 1.2em; }

        /* 🔵 Headings */
        h1, h3 {
            text-align: center;
            color: #007bff;
        }

        /* 🔵 Buttons */
        button[type="submit"] {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            box-shadow: 0px 3px 6px rgba(0,0,0,0.2);
        }

        button[type="submit"]:hover {
            background-color: #0056b3;
        }

        button[type="button"] {
            background-color: #555;
            color: white;
            padding: 8px 15px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }

        button[type="button"]:hover {
            background-color: #333;
        }
    </style>
</head>
<body>

    <!-- 🔹 LOGIN PAGE -->
    <div id="loginBox">
        <div class="login-container">
            <h2>Login</h2>
            <input type="text" id="username" placeholder="Username" required>
            <input type="password" id="password" placeholder="Password" required>
            <button onclick="checkLogin()">Login</button>
            <div id="errorMsg" class="error"></div>
        </div>
    </div>

    <!-- 🔹 BILL PAGE -->
    <div id="billPage">
        <h1>BILL ENTRY</h1>

        <form id="billForm" method="post" onsubmit="calculateTotals(event)">
            <div class="inline">
                <label>பெயர்: <span class="dot-line"><input name="name" required></span></label><br>
                <label>ஊர்: <span class="dot-line"><input name="place" required></span></label><br>
                <label>Phone: <span class="dot-line"><input name="phone" required></span></label><br>
            </div>
            <div class="inline right">
                <label>டோக்கன் No: <span class="dot-line"><input name="token_no" required></span></label><br>
                <label>தேதி: <span class="dot-line"><input name="date" type="date" required></span></label><br>
                <label>ரகம்: <span class="dot-line"><input name="category" required></span></label><br>
            </div>

            <h3>பட்டியல்</h3>
            <table id="entryTable">
                <thead>
                    <tr>
                        <th>S.No</th>
                        <th>பிளாஸ்டிக் பை</th>
                        <th>நார் பை</th>
                        <th>மொத்த எடை (in kg)</th>
                    </tr>
                </thead>
                <tbody id="tableBody">
                    <tr>
                        <td>1</td>
                        <td><input name="plastic_1" type="number" required></td>
                        <td><input name="nar_1" type="number" required></td>
                        <td><input name="weight_1" type="number" step="0.01" required></td>
                    </tr>
                </tbody>
            </table>

            <input type="hidden" name="total_rows" id="totalRows" value="1">

            <!-- Add Row Button -->
            <button type="button" id="addRowBtn" onclick="addRow()">+ Add Row</button>
            <br><br>

            <button type="submit">Generate Bill</button>
        </form>

        <div id="result" class="totals"></div>
        <button id="downloadPdfBtn" style="display:none;" onclick="downloadPDF()">Download as PDF</button>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>

    <script>
        // ✅ LOGIN FUNCTION
        function checkLogin() {
            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();
            const errorMsg = document.getElementById("errorMsg");

            if (username === "senthilkumar" && password === "Suguna@80") {
                document.getElementById("loginBox").style.display = "none";
                document.getElementById("billPage").style.display = "block";
            } else {
                errorMsg.textContent = "Invalid username or password!";
            }
        }

        // ✅ BILL ENTRY FUNCTIONS
        let rowCount = 1;

        function addRow() {
            rowCount++;
            const table = document.getElementById("tableBody");
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${rowCount}</td>
                <td><input name="plastic_${rowCount}" type="number" required></td>
                <td><input name="nar_${rowCount}" type="number" required></td>
                <td><input name="weight_${rowCount}" type="number" step="0.01" required></td>
            `;
            table.appendChild(row);
            document.getElementById("totalRows").value = rowCount;
        }

        function calculateTotals(event) {
            event.preventDefault();

            let totalPlastic = 0;
            let totalNar = 0;
            let totalWeight = 0;

            for (let i = 1; i <= rowCount; i++) {
                const plastic = parseInt(document.querySelector(`[name="plastic_${i}"]`)?.value || 0);
                const nar = parseInt(document.querySelector(`[name="nar_${i}"]`)?.value || 0);
                const weight = parseFloat(document.querySelector(`[name="weight_${i}"]`)?.value || 0);
                totalPlastic += plastic;
                totalNar += nar;
                totalWeight += weight;
            }

            const totalBags = totalPlastic + totalNar;
            const coolie = totalWeight * 6;

            // Hide the Add Row button after bill generation
            document.getElementById("addRowBtn").style.display = "none";

            document.getElementById("result").innerHTML = `
                <p><strong>மொத்த பைகள்:</strong> ${totalBags}</p>
                <p><strong>மொத்த எடைகள்:</strong> ${totalWeight.toFixed(2)} kg</p>
                <p><strong>கூலி:</strong> ₹ ${coolie.toFixed(2)}</p>
            `;
            document.getElementById("downloadPdfBtn").style.display = 'inline-block';
        }

        // ✅ PDF DOWNLOAD FUNCTION
        function downloadPDF() {
            const { jsPDF } = window.jspdf;
            const doc = new jsPDF();
            doc.setFontSize(16);
            doc.text("BILL ENTRY", 105, 10, null, null, "center");

            const name = document.querySelector('[name="name"]').value;
            const place = document.querySelector('[name="place"]').value;
            const phone = document.querySelector('[name="phone"]').value;
            const token_no = document.querySelector('[name="token_no"]').value;
            const date = document.querySelector('[name="date"]').value;
            const category = document.querySelector('[name="category"]').value;

            doc.setFontSize(12);
            doc.text(`பெயர்: ${name}`, 10, 30);
            doc.text(`ஊர்: ${place}`, 10, 40);
            doc.text(`Phone: ${phone}`, 10, 50);
            doc.text(`டோக்கன் No: ${token_no}`, 120, 30);
            doc.text(`தேதி: ${date}`, 120, 40);
            doc.text(`ரகம்: ${category}`, 120, 50);

            // Auto filename like RICEMILL_Bill_YYYY-MM-DD.pdf
            const today = new Date();
            const formattedDate = today.toISOString().split('T')[0];
            const fileName = `RICEMILL_Bill_${formattedDate}.pdf`;

            // Save (browser will ask where to save)
            doc.save(fileName);

            alert("📄 Your bill has been generated. Save it inside your 'RICEMILL' folder!");
        }
    </script>
</body>
</html>
