const express = require('express');
const {MongoClient} = require('mongodb');
const cors = require("cors");

const app=express();
app.use(cors());

//const uri="mongodb+srv://maximiliano:maximiliano1612@cluster0.ny0huus.mongodb.net/?appName=Cluster0"
const uri= "mongodb://maximiliano:maximiliano1612@ac-eolhicn-shard-00-00.ny0huus.mongodb.net:27017,ac-eolhicn-shard-00-01.ny0huus.mongodb.net:27017,ac-eolhicn-shard-00-02.ny0huus.mongodb.net:27017/?ssl=true&replicaSet=atlas-4jjdwu-shard-0&authSource=admin&appName=Cluster0"
const client= new MongoClient(uri);

async function main(){
    await client.connect();
    const db=client.db("sample_mflix")
    const movies = db.collection("movies");

    app.get("/movies", async (req, res)=> {
        const data = await movies
        .find({},{projection:{poster:1, title:1, fullplot:1}})
        .limit(60)
        .toArray();
        res.json(data)
    });
    app.listen(4000, ()=> console.log("Server running at http://202.5.96.57:4000"))
}

main().catch(console.error)