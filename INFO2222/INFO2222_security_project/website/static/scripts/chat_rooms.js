const enc = new TextEncoder("utf-8");
const dec = new TextDecoder("utf-8");
function convertArrayBufferToBase64(arrayBuffer) {
    return btoa(String.fromCharCode(...new Uint8Array(arrayBuffer)));
}
async function decryptMessage(privateKey, ciphertext) {
    result = await window.crypto.subtle.decrypt({name: "RSA-OAEP"}, privateKey, ciphertext);
    return dec.decode(result)
}

function encryptMessage(publicKey, msg) {
    // convert msg to arraybuffer
    msg = enc.encode(msg);
    // returned
    return window.crypto.subtle.encrypt({name: "RSA-OAEP"}, publicKey, msg);
}
async function importPrivateKey(jwKey) {
    return window.crypto.subtle.importKey(
        "jwk",
        jwKey,
        {name:"RSA-OAEP", hash:"SHA-256"},
        true,
        ["decrypt"]
    );
}
async function importPrivateSignKey(jwKey) {
    return window.crypto.subtle.importKey(
        "jwk",
        jwKey,
        {name:"RSA-PSS", hash:"SHA-256"},
        true,
        ["sign"]
    );
}

async function importPublicSignKey(jwKey) {
    return window.crypto.subtle.importKey(
        "jwk",
        jwKey,
        {name:"RSA-PSS", hash:"SHA-256"},
        true,
        ["verify"]
    );
}

async function importPublicKey(jwKey) {
    return window.crypto.subtle.importKey(
        "jwk",
        jwKey,
        {name:"RSA-OAEP", hash:"SHA-256"},
        true,
        ["encrypt"]
    );
}
async function signMsg(privateKey, msg) {
    msg = enc.encode(msg);
    return window.crypto.subtle.sign(
        {
            name: "RSA-PSS",
            saltLength: 8,
        },
        privateKey,
        msg
    );
}

async function verifyMsg(publicKey,signature,msg) {
    msg = enc.encode(msg);
    return window.crypto.subtle.verify(
        {
            name: "RSA-PSS",
            saltLength: 8,
        },
        publicKey,
        signature,
        msg
    );
}


function convertBase64ToArrayBuffer(base64) {
    return (new Uint8Array(atob(base64).split('').map(char => char.charCodeAt()))).buffer;
}


document.addEventListener('DOMContentLoaded', () => {
    async function decrypt_msg(){
        let list = document.getElementById("Message").querySelectorAll('dd')
        const request = indexedDB.open("myDatabase", 1);
        request.onerror = function (event) {
            console.error("An error occured with IndexedDB");
            console.error(event);
        };
        request.onsuccess = function (event) {
            var db = request.result;
            request_1 = db.transaction('user', "readonly").objectStore('user').get(username);
            request_1.onsuccess = async () => {
                PrK = await importPrivateKey(request_1.result.PrK)
                list.forEach((item, index) => {
                    decryptMessage(PrK, convertBase64ToArrayBuffer(item.innerText)).then(result => {
                        item.innerText = result;
                    })
                })
            }
        }
    }
    decrypt_msg()
    // connect to local db
    var socket = io.connect(location.protocol + '//' + document.domain + ':' + location.port);


    document.querySelector('#send_message').addEventListener('click', async function (event){
        event.preventDefault()

        const request = indexedDB.open("myDatabase",1);
        request.onerror = function (event){
            console.error("An error occured with IndexedDB");
            console.error(event);
        };
        request.onsuccess = function (event) {
            var db = request.result

            request_1 = db.transaction('user', "readonly").objectStore('user').get(username);
            request_1.onsuccess = async () => {
                let PrK = await importPublicKey(request_1.result.PbK)
                let encryptedMsg = await encryptMessage(PrK, document.querySelector("#user_message").value)
                let SPrk = await importPrivateSignKey(request_1.result.SPrK)
                let signature = await signMsg(SPrk, document.querySelector("#user_message").value)
                let history_msg  = convertArrayBufferToBase64(encryptedMsg)
                document.querySelector('#signature').value = convertArrayBufferToBase64(signature)
                request_2 = db.transaction('user', "readonly").objectStore('user').get(friendname);
                request_2.onsuccess = () => {
                    importPublicKey(request_2.result.PbK).then(result => {
                        const message = document.querySelector("#user_message").value
                        encryptMessage(result, message).then(result => {

                            socket.send({
                                'msg': {
                                    'owner': friendname,
                                    'receiver': friendname,
                                    'sender': username,
                                    'signature': document.querySelector("#signature").value,
                                    'data': convertArrayBufferToBase64(result)
                                },
                                'history': {
                                    'owner': username,
                                    'receiver': friendname,
                                    'sender': username,
                                    'signature': document.querySelector("#signature").value,
                                    'data': history_msg
                                }
                            })
                        })
                    })
                }
            }

        }
    })
});
