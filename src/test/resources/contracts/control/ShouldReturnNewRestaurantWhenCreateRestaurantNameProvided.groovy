import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description "Should return new Restaurant when restaurant name provided"
    request {
        method POST()
        url("/register") {
            body(
                    "Mama Mia Restaurante"
            )
        }
    }
    response {
        body(
                id: 1,
                name: "Mama Mia Restaurante",
                surname: "bee",
                open: false,
                archived: false,
                menu: []
        )
        status 200
    }
}