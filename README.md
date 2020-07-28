    # Ryanair Flights and Tickets (technical test app)
    
    ## Tasks
    Develop the following app (Java/Kotlin) with following features:
    A form with following input fields so user can search for flights:
      - Origin station
      - Destination station
      - Departure date
      - Number of adults
      - Number of teens
      - Number of children
    
    The form should include a search button.
    
    After clicking on the search button, the availability call should be made.
    Response should be present in a form of list, each item should contain:
      - Flight date
      - Flight number
      - Duration
      - Regular fare price with the currency
    
    On the toolbar present the names of the origin and the destination.
    After clicking on an item, open a flight summary screen where values for
      - origin
      - destination
      - infantsLeft
      - fareClass
      - discountInPercent 
    
    are presented.
    
    Additional task (for extra points):
    Below the toolbar add a slider which allows to filter out the flights with a price higher than selected value. 
    Set the default slider value to 150 and the max value to 1000.
    Filter should be applied in two situations:
      - When flights are presented after the search button is clicked
      - When a user interacts with the slider
    
    # My sketch!
    
     - Kotlin
     - Coroutine
     - Moshi for JSON parsing
     - No Clean Architecture
     - No MVP or MVVM to simplify architecture
     - No Retrofit, DI and etc
     - No any Tests
    
    The example deliberately does not use the MVP, MVVM, MVI or similar to simplify the architecture. In a real application, the architecture should be built according to generally accepted patterns
    
    To simplify:
    - There are no validations for the input, so the flight search query will run as is
    - There are practically no checks for the correctness of network requests and error handling
    - Sometimes silly and funny code is used
