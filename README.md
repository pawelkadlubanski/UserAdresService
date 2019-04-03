Tworzymy mikroserwis do przetrzymywania katalogu z klientami i ich adresami.


Wymagania:

- Klient może posiadać kilka adresów

- Aplikacja wystawia restowe endpointy. Dzięki nim można 

przejrzeć listę wszystkich klientów i ich adresów

dodać nowego klienta

dodać nowy adres do znanego klienta

- Aplikacja wystawia swaggera z tymi endpointami

- Dane wejściowe powinny mieć podstawową walidację

- Dane sa zapisane w bazie in-memory

- Aplikacja powinna być zabezpieczona przez basic authentication z loginem/hasłem admin/admin

- Aplikacja może zostać uruchomiona jako obraz dockera

- Kod powinien znaleźć się w repozytorium gita


Nie trzeba implementować:

- Edycji/usuwania klienta/adresu

- UI