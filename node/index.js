const http = require('http')

const port = 8080

http.createServer(function (req, res) {
    const n = parseInt(req.url.substring(1))
    if (n < 2) {
        res.end('' + n)
    } else {
        Promise.all([
            fibonacci(n - 1),
            fibonacci(n - 2)
        ]).then(function (ns) {
            const fibN = ns[0] + ns[1]
            res.end('' + fibN)
        })
    }
}).listen(port)

function fibonacci(n) {
    return new Promise(function (resolve, reject) {
        const request = http.get(`http://localhost:${port}/${n}`, function (res) {
            let rawData = ''
            res.on('data', (chunk) => { rawData += chunk })
            res.on('end', () => { resolve(parseInt(rawData)) })
        })
    })
}