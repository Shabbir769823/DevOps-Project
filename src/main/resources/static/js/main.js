// Client-side interactions for Employee Web App

document.addEventListener('DOMContentLoaded', () => {
    // Automatically dismiss alert notifications after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.remove();
            }, 500);
        }, 5000);
    });

    // Confirmation for delete actions
    const deleteButtons = document.querySelectorAll('.btn-delete-confirm');
    deleteButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            if (!confirm('Are you sure you want to delete this record? This action cannot be undone.')) {
                e.preventDefault();
            }
        });
    });

    // Chat Notification Badge Polling
    const badge = document.getElementById('chat-badge');
    if (badge) {
        function updateGlobalBadge() {
            fetch('/chat/unread-count')
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error('Not logged in');
                })
                .then(count => {
                    if (count > 0) {
                        badge.innerText = count;
                        badge.classList.remove('d-none');
                    } else {
                        badge.classList.add('d-none');
                    }
                })
                .catch(err => {
                    // Fail silently
                });
        }
        updateGlobalBadge();
        setInterval(updateGlobalBadge, 3000);
    }
});

