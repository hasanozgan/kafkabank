package main

import (
	"fmt"
	"log"

	"github.com/Mongey/ksql/ksql"
)

func main() {
	fmt.Println("hello")

	c := ksql.NewClient("http://localhost:8088")
	log.Println("=>>> Streams")
	streams, err := c.ListStreams()
	if err != nil {
		log.Fatal(err)
	}
	for i, v := range streams {
		log.Printf("Stream %d: %s", i, v.Name)
	}
}
