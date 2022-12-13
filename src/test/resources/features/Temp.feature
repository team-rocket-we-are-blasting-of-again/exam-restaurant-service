Feature: Can create restaurant

  Scenario Outline:
    Given a bowling game
    When i roll <rolls> times and hit <amount> each time
    Then my final score is <finalScore>

    Examples:
      | rolls | amount | finalScore |
      | 20    | 0      | 0          |
      | 21    | 4      | 80         |
      | 3     | 5      | 20         |

