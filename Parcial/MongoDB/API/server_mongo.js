const express = require('express');
const {MongoClient} = requires('mongodb');
const cors = require("cors");

const app=express();
app.use(cors());

const uri="mongodb+srv://maximiliano:maximiliano1612@cluster0.ny0huus.mongodb.net/?appName=Cluster0"
const client= new MongoClient(uri);

async function main(){
    await client.connect();
    const db=client.db("sample_mflix")
    const movies = db.collection("movies");

    app.get("/movies", async (req, res)=> {
        const data = await movies
        .find({},{projection:{poster1, title:1, fullplot:1}})
        .limit(60)
        .toArray();
        res.json(data)
    });
    app.listen(4000, ()=> console.log("Server running at http://localhost:4000"))
}

main().catch(console.error)