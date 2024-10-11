$(document).ready(function () {
    $('.editBtn').on('click', function (event) {
        event.preventDefault();

        const userId = $(this).data('id');
        $.ajax({
            url: `/users/${userId}`,
            method: 'GET',
            success: function (data) {
                $.ajax({
                    method: 'GET',
                    success: function (modalHtml) {
                        $('#modalContainer').html(modalHtml);

                        // Uzupełnianie danych w formularzu modalu
                        $('#editUserModal #userId').val(data.id);
                        $('#editUserModal #firstName').val(data.firstName);
                        $('#editUserModal #lastName').val(data.lastName);
                        $('#editUserModal #phoneNumber').val(data.phoneNumber);
                        $('#editUserModal #email').val(data.email);

                        // Otwieranie modalu
                        $('#editUserModal').modal('show');
                    }
                });
            },
            error: function () {
                console.error('Wystąpił błąd podczas pobierania danych użytkownika.');
            }
        });
    });

    $(document).on('submit', '#editUserForm', function (event) {
        event.preventDefault();
        $.ajax({
            url: '/users/edit',
            method: 'POST',
            data: $(this).serialize(),
            success: function () {
                $('#editUserModal').modal('hide');
                // Opcjonalnie, odśwież listę użytkowników
                window.location.href = '/users?updated=true';
            },
            error: function () {
                console.error('Wystąpił błąd podczas aktualizacji danych użytkownika.');
            }
        });
    });
});