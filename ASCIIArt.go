package main

import "fmt"
import "os"
import "bufio"
import "strings"

func main() {
    scanner := bufio.NewScanner(os.Stdin)

    var letterWidth int
    scanner.Scan()
    fmt.Sscan(scanner.Text(), &letterWidth)
    
    var letterHeight int
    scanner.Scan()
    fmt.Sscan(scanner.Text(), &letterHeight)
    
    asciiLetters := make([]string, letterHeight)
    
    scanner.Scan()
    text := strings.ToUpper(scanner.Text())
    for i := 0; i < letterHeight; i++ {
        scanner.Scan()
        asciiLetters[i] = scanner.Text()
    }

    for row := range asciiLetters {
        for letter := range text {
            letterNumber := int((text[letter] - 65))
            if (letterNumber > 26) {
                letterNumber = 26
            }
            letterNumber = int(letterNumber) * letterWidth
            fmt.Print(asciiLetters[row][letterNumber : letterNumber + letterWidth])
        }
        fmt.Println()
    }
}
