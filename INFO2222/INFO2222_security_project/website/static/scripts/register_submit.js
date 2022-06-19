let keygen = window.crypto.subtle.generateKey(
    {
        name: "RSA-OAEP",
        modulusLength: 1024,
        publicExponent: new Uint8Array([1, 0, 1]),
        hash: "SHA-256"
    },
    true,
    ["encrypt", "decrypt"]
)

let signgen =  window.crypto.subtle.generateKey(
    {
        name: "RSA-PSS",
        // Consider using a 4096-bit key for systems that require long-term security
        modulusLength: 1024,
        publicExponent: new Uint8Array([1, 0, 1]),
        hash: "SHA-256",
    },
    true,
    ["sign", "verify"]
)

let captcha = new Array();

function createCaptcha() {
    const activeCaptcha = document.getElementById("captcha");
    for (q = 0; q < 6; q++) {
        if (q % 2 == 0) {
            captcha[q] = String.fromCharCode(Math.floor(Math.random() * 26 + 65));
        } else {
            captcha[q] = Math.floor(Math.random() * 10 + 0);
        }
    }
    theCaptcha = captcha.join("");
    activeCaptcha.innerHTML = `${theCaptcha}`;
}

function validateCaptcha() {
    const errCaptcha = document.getElementById("errCaptcha");
    const reCaptcha = document.getElementById("reCaptcha");
    recaptcha = reCaptcha.value;
    let validateCaptcha = 0;
    for (var z = 0; z < 6; z++) {
        if (recaptcha.charAt(z) != captcha[z]) {
            validateCaptcha++;
        }
    }
    if (recaptcha == "") {
        errCaptcha.innerHTML = "Re-Captcha must be filled";
    } else if (validateCaptcha > 0 || recaptcha.length > 6) {
        errCaptcha.innerHTML = "Wrong captcha";
        document.getElementById("send_message").style = "visibility: hidden;"

    } else {
        errCaptcha.innerHTML = "Done";
        createCaptcha()
        document.getElementById("send_message").style = "visibility: visible;"
    }
}


function createDB(){
    const request = indexedDB.open("myDatabase",1);
    request.onerror = function (event){
        console.error("An error occured with IndexedDB");
        console.error(event);
    };
    request.onupgradeneeded = function (){
        let db = request.result;
        let object_store = db.createObjectStore('user',{keyPath: 'user_name'});
        object_store.createIndex('user_name','user_name',{unique: true});
        object_store.createIndex('PbK','PbK',{unique: true});
        object_store.createIndex('PrK','PrK',{unique: true});
        object_store.createIndex('SPbK','SPbK',{unique: true});
        object_store.createIndex('SPrK','SPrK',{unique: true});
        object_store.transaction.oncomplete = (e) =>{
            console.log('database created');
        }
    }
}


function addUser(user){
    const request = indexedDB.open("myDatabase",1);
    request.onerror = function (event){
        console.error("An error occured with IndexedDB");
        console.error(event);
    };
    request.onsuccess = function (event) {
        let db = request.result;

        const transaction = db.transaction('user', 'readwrite');

        const objectStore = transaction.objectStore('user');

        let add_request = objectStore.put(user);

        add_request.onsuccess = () => {
            // request.result contains key of the added object
            console.log(`New user added, email: ${request.result}`);
        }

        add_request.onerror = (err) => {
            console.error(`Error to add new user: ${err}`)
        }
    }
}


async function register_submit() {
    let keypair = await keygen
    createDB();
    let PbK = await window.crypto.subtle.exportKey(
        "jwk",
        keypair.publicKey
    )
    document.querySelector('#public_key').value = JSON.stringify(PbK);

    let PrK = await window.crypto.subtle.exportKey(
        "jwk",
        keypair.privateKey)

    let signKeyPair = await signgen

    let SPbK = await window.crypto.subtle.exportKey(
                    "jwk",
                    signKeyPair.publicKey
                )
    document.querySelector('#public_sign_key').value = JSON.stringify(SPbK);

    let SPrK = await  window.crypto.subtle.exportKey(
                    "jwk",
                    signKeyPair.privateKey
                )

    const register_user = {
        user_name: document.querySelector('#user_name').value,
        PbK: PbK,
        PrK: PrK,
        SPbK: SPbK,
        SPrK: SPrK
    }
    addUser(register_user);
    document.getElementById('form1').submit()


}


